package it.polito.wa2.orchestrator.controllers

import it.polito.wa2.dto.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture
import javax.validation.Valid

@RestController
class OrchestratorController(
    val transactionCreateTemplate: ReplyingKafkaTemplate<String, TransactionRequestDTO, TransactionResponseDTO>,
    val inventoryChangeTemplate: ReplyingKafkaTemplate<String, InventoryChangeRequestDTO, InventoryChangeResponseDTO>,
    val inventoryReturnTemplate: ReplyingKafkaTemplate<String, InventoryCancelOrderRequestDTO, InventoryCancelOrderResponseDTO>,
    val transactionCreateRollbackTemplate: KafkaTemplate<String, WalletRequestDTO>,
    val inventoryChangeRollbackTemplate: KafkaTemplate<String, InventoryChangeResponseDTO>,
    val inventoryReturnRollbackTemplate: KafkaTemplate<String, InventoryCancelOrderRequestDTO>
) {

    @PostMapping("/orders")
    fun createOrder(@Valid @RequestBody requestDTO: OrderCreateOrchestratorRequestDTO): OrderCreateOrchestratorResponseDTO {
        println("Received request")
        println(requestDTO)

        val walletRequestDTO = TransactionRequestDTO(
            requestDTO.orderId,
            requestDTO.buyerId,
            -requestDTO.totalPrice
        )

        val warehouseRequestDTO = InventoryChangeRequestDTO(
            requestDTO.totalPrice,
            requestDTO.items
        )

        return runCreationSaga(requestDTO.buyerId, walletRequestDTO, warehouseRequestDTO)
    }

    @DeleteMapping("/orders/{orderID}")
    fun cancelOrder(@Valid @RequestBody requestDTO: OrderDeleteOrchestratorRequestDTO): OrderDeleteOrchestratorResponseDTO {
        println("Received request")
        println(requestDTO)

        val walletRequestDTO = TransactionRequestDTO(
            requestDTO.orderId,
            requestDTO.buyerId,
            requestDTO.totalPrice
        )

        val warehouseRequestDTO = InventoryCancelOrderRequestDTO(
            requestDTO.items
        )

        return runDeletionSaga(walletRequestDTO, warehouseRequestDTO)
    }

    private fun getTransactionCreateFuture(transactionRequestDTO: TransactionRequestDTO)
            : CompletableFuture<ConsumerRecord<String, TransactionResponseDTO>> {
        val transactionRecord = ProducerRecord<String, TransactionRequestDTO>(
            "transaction-create",
            transactionRequestDTO
        )

        transactionRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "transaction-created".toByteArray()))

        return transactionCreateTemplate.sendAndReceive(transactionRecord).completable()
    }

    private fun getTransactionCreatedRollbackFuture(
        walletResponse: TransactionResponseDTO,
        walletRequestDTO: TransactionRequestDTO
    ): ListenableFuture<SendResult<String, WalletRequestDTO>> {
        println("Rolling back created transaction")

        val walletRollbackRecord = ProducerRecord<String, WalletRequestDTO>(
            "transaction-created-rollback",
            WalletRequestDTO(
                walletRequestDTO.buyerId,
                walletRequestDTO.orderId,
                walletRequestDTO.amount,
                walletResponse.transactionId
            )
        )
        return transactionCreateRollbackTemplate.send(walletRollbackRecord)
    }

    private fun getInventoryChangeFuture(warehouseRequestDTO: InventoryChangeRequestDTO)
            : CompletableFuture<ConsumerRecord<String, InventoryChangeResponseDTO>> {
        val warehouseRecord =
            ProducerRecord<String, InventoryChangeRequestDTO>(
                "inventory-change",
                warehouseRequestDTO
            )
        warehouseRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "inventory-changed".toByteArray()))

        return inventoryChangeTemplate.sendAndReceive(warehouseRecord).completable()
    }

    private fun getInventoryChangedRollbackFuture(
        warehouseResponse: InventoryChangeResponseDTO,
    ): ListenableFuture<SendResult<String, InventoryChangeResponseDTO>> {
        println("Rolling back inventory change")
        val warehouseRollbackRecord = ProducerRecord<String, InventoryChangeResponseDTO>(
            "inventory-changed-rollback",
            warehouseResponse
        )

        return inventoryChangeRollbackTemplate.send(warehouseRollbackRecord)
    }

    private fun getInventoryReturnFuture(warehouseRequestDTO: InventoryCancelOrderRequestDTO)
            : CompletableFuture<ConsumerRecord<String, InventoryCancelOrderResponseDTO>> {
        val warehouseRecord =
            ProducerRecord<String, InventoryCancelOrderRequestDTO>(
                "inventory-return",
                warehouseRequestDTO
            )
        warehouseRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "inventory-returned".toByteArray()))

        return inventoryReturnTemplate.sendAndReceive(warehouseRecord).completable()
    }

    private fun getInventoryReturnedRollbackFuture(
        warehouseRequestDTO: InventoryCancelOrderRequestDTO
    ): ListenableFuture<SendResult<String, InventoryCancelOrderRequestDTO>> {
        println("Rolling back inventory return")

        val warehouseRollbackRecord = ProducerRecord<String, InventoryCancelOrderRequestDTO>(
            "inventory-changed-rollback",
            warehouseRequestDTO
        )

        return inventoryReturnRollbackTemplate.send(warehouseRollbackRecord)
    }

    private fun runCreationSaga(
        userId: Long,
        transactionCreateRequestDTO: TransactionRequestDTO,
        inventoryChangeRequestDTO: InventoryChangeRequestDTO
    ): OrderCreateOrchestratorResponseDTO {

        val transactionCreateFuture = getTransactionCreateFuture(transactionCreateRequestDTO)
        val inventoryChangeFuture = getInventoryChangeFuture(inventoryChangeRequestDTO)

        var responseItems: List<InventoryChangeResponseProductDTO> = listOf()

        try {
            println("Sending both requests")
            val allFuturesResult = CompletableFuture.allOf(transactionCreateFuture, inventoryChangeFuture)

            // Wait for both responses to be resolved
            allFuturesResult.get()

        } catch (e: Exception) {
            println("Exception thrown while creating order")
            println(e)
        }

        val didWalletFail = transactionCreateFuture.isCompletedExceptionally
        val didWarehouseFail = inventoryChangeFuture.isCompletedExceptionally

        var isTransactionCreated = false
        var isInventoryChanged = false

        var inventoryChangeResponseDTO: InventoryChangeResponseDTO? = null
        var transactionCreateResponseDTO: TransactionResponseDTO? = null


        if (didWarehouseFail) {
            println("Inventory change response NOT received")
        } else {
            println("Inventory change response received")

            inventoryChangeResponseDTO = inventoryChangeFuture.get().value()
            println(inventoryChangeResponseDTO)

            responseItems = inventoryChangeResponseDTO.items

            isInventoryChanged = inventoryChangeResponseDTO.isConfirmed
        }

        if (didWalletFail) {
            println("Transaction create response NOT received")
        } else {
            println("Transaction create response received")
            transactionCreateResponseDTO = transactionCreateFuture.get().value()
            println(transactionCreateResponseDTO)

            isTransactionCreated = transactionCreateResponseDTO.wasCharged
        }

        if (isTransactionCreated && isInventoryChanged) {
            println("Order created successfully")
        } else {

            if (isTransactionCreated) {
                getTransactionCreatedRollbackFuture(transactionCreateResponseDTO!!, transactionCreateRequestDTO)
            } else if (isInventoryChanged) {
                getInventoryChangedRollbackFuture(inventoryChangeResponseDTO!!)
            } else {
                println("Everything failed -> nothing to rollback!")
            }
        }

        return OrderCreateOrchestratorResponseDTO(
            userId,
            responseItems,
            isTransactionCreated && isInventoryChanged
        )
    }

    private fun runDeletionSaga(
        transactionCreateRequestDTO: TransactionRequestDTO,
        inventoryReturnRequestDTO: InventoryCancelOrderRequestDTO
    ): OrderDeleteOrchestratorResponseDTO {

        val transactionCreateFuture = getTransactionCreateFuture(transactionCreateRequestDTO)
        val inventoryReturnFuture = getInventoryReturnFuture(inventoryReturnRequestDTO)


        try {
            println("Sending both requests")
            val allFuturesResult = CompletableFuture.allOf(transactionCreateFuture, inventoryReturnFuture)

            // Wait for both responses to be resolved
            allFuturesResult.get()

        } catch (e: Exception) {
            println("Exception thrown while cancelling order")
            println(e)
        }

        val didWalletFail = transactionCreateFuture.isCompletedExceptionally
        val didWarehouseFail = inventoryReturnFuture.isCompletedExceptionally

        var isTransactionCreated = false
        var isInventoryReturned = false

        val inventoryReturnResponse: InventoryCancelOrderResponseDTO?
        var transactionCreateResponseDTO: TransactionResponseDTO? = null

        if (didWarehouseFail) {
            println("Inventory return response NOT received")
        } else {
            println("Inventory return response received")

            inventoryReturnResponse = inventoryReturnFuture.get().value()
            println(inventoryReturnResponse)

            isInventoryReturned = inventoryReturnResponse.isCancelled
        }

        if (didWalletFail) {
            println("Transaction create response NOT received")
        } else {
            println("Transaction create response received")
            transactionCreateResponseDTO = transactionCreateFuture.get().value()
            println(transactionCreateResponseDTO)

            isTransactionCreated = transactionCreateResponseDTO.wasCharged
        }

        if (isTransactionCreated && isInventoryReturned) {
            println("Order cancelled successfully")
        } else {

            if (isTransactionCreated) {
                getTransactionCreatedRollbackFuture(transactionCreateResponseDTO!!, transactionCreateRequestDTO)
            } else if (isInventoryReturned) {
                getInventoryReturnedRollbackFuture(inventoryReturnRequestDTO)
            } else {
                println("Everything failed -> nothing to rollback!")
            }
        }



        return OrderDeleteOrchestratorResponseDTO(
            isTransactionCreated && isInventoryReturned
        )
    }
}