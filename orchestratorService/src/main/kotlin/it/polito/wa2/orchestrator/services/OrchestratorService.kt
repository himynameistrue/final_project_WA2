package it.polito.wa2.orchestrator.services

import it.polito.wa2.dto.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class OrchestratorService(
    val walletTemplate: ReplyingKafkaTemplate<String, TransactionRequestDTO, TransactionResponseDTO>,
    val warehouseTemplate: ReplyingKafkaTemplate<String, InventoryChangeRequestDTO, InventoryChangeResponseDTO>,
    val walletRollbackTemplate: KafkaTemplate<String, WalletRequestDTO>,
    val warehouseRollbackTemplate: KafkaTemplate<String, InventoryChangeRequestDTO>,
) {


    private fun getTransactionRequestReplyFuture(transactionRequestDTO: TransactionRequestDTO): CompletableFuture<ConsumerRecord<String, TransactionResponseDTO>> {
        val transactionRecord = ProducerRecord<String, TransactionRequestDTO>(
            "order-create-orchestrator-to-wallet",
            transactionRequestDTO
        )

        transactionRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-wallet-to-orchestrator".toByteArray()))

        return walletTemplate.sendAndReceive(transactionRecord).completable()
    }

    private fun getInventoryChangeRequestReplyFuture(warehouseRequestDTO: InventoryChangeRequestDTO): CompletableFuture<ConsumerRecord<String, InventoryChangeResponseDTO>> {
        val warehouseRecord =
            ProducerRecord<String, InventoryChangeRequestDTO>(
                "order-create-orchestrator-to-warehouse",
                warehouseRequestDTO
            )
        warehouseRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-warehouse-to-orchestrator".toByteArray()))

        return warehouseTemplate.sendAndReceive(warehouseRecord).completable()
    }

    fun runCreationSaga(
        userId: Long,
        walletRequestDTO: TransactionRequestDTO,
        warehouseRequestDTO: InventoryChangeRequestDTO
    ): OrderCreateOrchestratorResponseDTO {


        val walletReplyFuture = getTransactionRequestReplyFuture(walletRequestDTO)
        val warehouseReplyFuture = getInventoryChangeRequestReplyFuture(warehouseRequestDTO)

        lateinit var responseItems: List<InventoryChangeResponseProductDTO>
        var isSuccessful = false

        try {
            println("Sending both requests")
            val allFuturesResult = CompletableFuture.allOf(walletReplyFuture, warehouseReplyFuture)

            // Wait for both responses to be resolved
            allFuturesResult.get()

            // Retrieve content of both responses
            val walletResponse = walletReplyFuture.get().value()
            val warehouseResponse = warehouseReplyFuture.get().value()
            println("Wallet response:")
            println(walletResponse)

            println("Warehouse response:")
            println(warehouseResponse)

            responseItems = warehouseResponse.items

            val successfulWalletResponse = walletResponse.wasCharged
            val successfulWarehouseRequest = warehouseResponse.isConfirmed
            if (successfulWalletResponse && successfulWarehouseRequest) {
                println("Order created successfully")
                isSuccessful = true
            } else if (successfulWalletResponse) {
                handleWarehouseServiceFailed(walletResponse, walletRequestDTO)
            } else {
                handleWalletServiceFailed(warehouseResponse, warehouseRequestDTO)
            }

        } catch (e: Exception) {
            println(e)

            val didWalletFail = walletReplyFuture.isCompletedExceptionally
            val didWarehouseFail = warehouseReplyFuture.isCompletedExceptionally

            responseItems = if (didWarehouseFail) {
                listOf()
            } else {
                warehouseReplyFuture.get().value().items
            }

            if (didWalletFail && !didWarehouseFail) {
                val warehouseResponse = warehouseReplyFuture.get().value()

                handleWalletServiceFailed(warehouseResponse, warehouseRequestDTO)
            } else if (didWarehouseFail && !didWalletFail) {
                val walletResponse = walletReplyFuture.get().value()

                handleWarehouseServiceFailed(walletResponse, walletRequestDTO)

            }
            println("End of catch block")
        }

        return OrderCreateOrchestratorResponseDTO(
            userId,
            responseItems,
            isSuccessful
        )
    }

    fun runDeletionSaga(
        userId: Long,
        walletRequestDTO: TransactionRequestDTO,
        warehouseRequestDTO: InventoryChangeRequestDTO
    ) {
        /*val walletReplyFuture = getTransactionRequestReplyFuture(walletRequestDTO)
        val warehouseReplyFuture = getInventoryChangeRequestReplyFuture(warehouseRequestDTO)

        lateinit var responseItems: List<InventoryChangeResponseProductDTO>
        var isSuccessful = false

        try {
            println("Sending both requests")
            val allFuturesResult = CompletableFuture.allOf(walletReplyFuture, warehouseReplyFuture)

            // Wait for both responses to be resolved
            allFuturesResult.get()

            // Retrieve content of both responses
            val walletResponse = walletReplyFuture.get().value()
            val warehouseResponse = warehouseReplyFuture.get().value()
            println("Wallet response:")
            println(walletResponse)

            println("Warehouse response:")
            println(warehouseResponse)

            responseItems = warehouseResponse.items

            if (walletResponse.wasCharged
                && warehouseResponse.isConfirmed
            ) {
                println("Order created successfully")
                isSuccessful = true
            }

        } catch (e: Exception) {
            println(e)

            val didWalletFail = walletReplyFuture.isCompletedExceptionally
            val didWarehouseFail = warehouseReplyFuture.isCompletedExceptionally

            responseItems = if (didWarehouseFail) {
                listOf()
            } else {
                warehouseReplyFuture.get().value().items
            }

            if (didWalletFail && !didWarehouseFail) {
                println("Wallet service failed")

                val warehouseResponse = warehouseReplyFuture.get().value()

                if (!warehouseResponse.isConfirmed) {
                    println("Warehouse did not respond successfully -> nothing to roll back")
                } else {
                    println("Rolling back warehouse")
                    val warehouseRollbackRecord = ProducerRecord<String, InventoryChangeRequestDTO>(
                        "order-create-rollback-orchestrator-to-warehouse",
                        warehouseRequestDTO
                    )

                    warehouseRollbackTemplate.send(warehouseRollbackRecord)
                }
            } else if (didWarehouseFail && !didWalletFail) {
                println("Warehouse service failed")

                val walletResponse = walletReplyFuture.get().value()

                if (!walletResponse.wasCharged) {
                    println("Wallet did not respond successfully -> nothing to roll back")
                } else {
                    println("Rolling back wallet")
                    val walletRollbackRecord = ProducerRecord<String, TransactionRequestDTO>(
                        "order-create-rollback-orchestrator-to-wallet",
                        walletRequestDTO
                    )
                    walletRollbackTemplate.send(walletRollbackRecord)
                }
            }
            println("End of catch block")
        }

        return OrderCreateOrchestratorResponseDTO(
            userId,
            responseItems,
            isSuccessful
        )*/
    }

    fun handleWalletServiceFailed(
        warehouseResponse: InventoryChangeResponseDTO,
        warehouseRequestDTO: InventoryChangeRequestDTO
    ) {
        println("Wallet service failed")

        if (!warehouseResponse.isConfirmed) {
            println("Warehouse did not respond successfully -> nothing to roll back")
        } else {
            println("Rolling back warehouse")
            val warehouseRollbackRecord = ProducerRecord<String, InventoryChangeRequestDTO>(
                "order-create-rollback-orchestrator-to-warehouse",
                warehouseRequestDTO
            )

            warehouseRollbackTemplate.send(warehouseRollbackRecord)
        }
    }

    fun handleWarehouseServiceFailed(walletResponse: TransactionResponseDTO, walletRequestDTO: TransactionRequestDTO) {
        println("Warehouse service failed")

        if (!walletResponse.wasCharged) {
            println("Wallet did not respond successfully -> nothing to roll back")
        } else {
            println("Rolling back wallet")
            val walletRollbackRecord = ProducerRecord<String, WalletRequestDTO>(
                "order-create-rollback-orchestrator-to-wallet",
                WalletRequestDTO(
                    walletRequestDTO.buyerId,
                    walletRequestDTO.orderId,
                    walletRequestDTO.amount,
                    walletResponse.transactionId
                )
            )
            walletRollbackTemplate.send(walletRollbackRecord)
        }
    }
}