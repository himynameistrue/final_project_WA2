package it.polito.wa2.orchestrator.handlers

import it.polito.wa2.dto.*
import it.polito.wa2.enums.InventoryStatus
import it.polito.wa2.enums.OrderStatus
import it.polito.wa2.enums.PaymentStatus
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.SendTo
import java.util.concurrent.CompletableFuture.allOf

@Configuration
class OrderCreatedEventHandler(
    val walletTemplate: ReplyingKafkaTemplate<String, OrderCreateWalletRequestDTO, OrderCreateWalletResponseDTO>,
    val warehouseTemplate: ReplyingKafkaTemplate<String, OrderCreateWarehouseRequestDTO, OrderCreateWarehouseResponseDTO>,
    val walletRollbackTemplate: KafkaTemplate<String, OrderCreateWalletRequestDTO>,
    val warehouseRollbackTemplate: KafkaTemplate<String, OrderCreateWarehouseRequestDTO>,
    ) {

/*    @KafkaListener(topics = ["order-create-order-to-orchestrator"], groupId = "orchestrator-group")
    @SendTo*/
    fun consumer(requestDTO: OrderCreateOrchestratorRequestDTO): OrderCreateOrchestratorResponseDTO {
        println("Received request")
        println(requestDTO)

        val walletRequestDTO = OrderCreateWalletRequestDTO(requestDTO.orderId, requestDTO.buyerId, requestDTO.totalPrice)
        val warehouseRequestDTO = OrderCreateWarehouseRequestDTO(requestDTO.totalPrice, requestDTO.items)

        val walletRecord =
            ProducerRecord<String, OrderCreateWalletRequestDTO>("order-create-orchestrator-to-wallet", walletRequestDTO)
        walletRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-wallet-to-orchestrator".toByteArray()))


        val warehouseRecord =
            ProducerRecord<String, OrderCreateWarehouseRequestDTO>("order-create-orchestrator-to-warehouse", warehouseRequestDTO)
        warehouseRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-warehouse-to-orchestrator".toByteArray()))

        val walletReplyFuture = walletTemplate.sendAndReceive(walletRecord).completable()
        val warehouseReplyFuture = warehouseTemplate.sendAndReceive(warehouseRecord).completable()

        lateinit var responseItems: List<OrderCreateWarehouseResponseProductDTO>
        var isSuccessful = false

        try {
            println("Sending both requests")
            val allFuturesResult = allOf(walletReplyFuture, warehouseReplyFuture)

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

            if(didWarehouseFail){
                responseItems = listOf()
            }

            if (didWalletFail && !didWarehouseFail) {
                println("Wallet service failed")

                val warehouseResponse = warehouseReplyFuture.get().value()

                if(!warehouseResponse.isConfirmed){
                    println("Warehouse did not respond successfully -> nothing to roll back")
                } else {
                    println("Rolling back warehouse")
                    val warehouseRollbackRecord = ProducerRecord<String, OrderCreateWarehouseRequestDTO>(
                        "order-create-rollback-orchestrator-to-warehouse",
                        warehouseRequestDTO
                    )

                    println("Sending warehouse rollback request")
                    warehouseRollbackTemplate.send(warehouseRollbackRecord)
                }
            } else if (didWarehouseFail && !didWalletFail) {
                println("Warehouse service failed")

                val walletResponse = walletReplyFuture.get().value()

                if(!walletResponse.wasCharged){
                    println("Wallet did not respond successfully -> nothing to roll back")
                } else {
                    println("Rolling back wallet")
                    val walletRollbackRecord = ProducerRecord<String, OrderCreateWalletRequestDTO>(
                        "order-create-rollback-orchestrator-to-wallet",
                        walletRequestDTO
                    )

                    println("Sending wallet rollback request")
                    walletRollbackTemplate.send(walletRollbackRecord)
                }
            }
            println("End of catch block")
        }




        return OrderCreateOrchestratorResponseDTO(
            requestDTO.buyerId,
            responseItems,
            isSuccessful
        )
    }
}