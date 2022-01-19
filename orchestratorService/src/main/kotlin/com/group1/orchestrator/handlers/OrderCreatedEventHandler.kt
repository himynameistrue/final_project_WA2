package com.group1.orchestrator.handlers

import com.group1.dto.*
import com.group1.enums.InventoryStatus
import com.group1.enums.OrderStatus
import com.group1.enums.PaymentStatus
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
    val walletTemplate: ReplyingKafkaTemplate<String, WalletRequestDTO, WalletResponseDTO>,
    val warehouseTemplate: ReplyingKafkaTemplate<String, WarehouseRequestDTO, WarehouseResponseDTO>,
    val walletRollbackTemplate: KafkaTemplate<String, WalletRequestDTO>,
    val warehouseRollbackTemplate: KafkaTemplate<String, WarehouseRequestDTO>,
    ) {

    @KafkaListener(topics = ["order-create-order-to-orchestrator"], groupId = "orchestrator-group")
    @SendTo
    fun consumer(requestDTO: OrchestratorRequestDTO): OrchestratorResponseDTO? {
        println("Received request")
        println(requestDTO)

        val walletRequestDTO = WalletRequestDTO(requestDTO.userId, requestDTO.orderId, requestDTO.amount)
        val warehouseRequestDTO = WarehouseRequestDTO(requestDTO.userId, requestDTO.productId, requestDTO.orderId)

        val walletRecord =
            ProducerRecord<String, WalletRequestDTO>("order-create-orchestrator-to-wallet", walletRequestDTO)
        walletRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-wallet-to-orchestrator".toByteArray()))


        val warehouseRecord =
            ProducerRecord<String, WarehouseRequestDTO>("order-create-orchestrator-to-warehouse", warehouseRequestDTO)
        warehouseRecord.headers()
            .add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-warehouse-to-orchestrator".toByteArray()))

        val walletReplyFuture = walletTemplate.sendAndReceive(walletRecord).completable()
        val warehouseReplyFuture = warehouseTemplate.sendAndReceive(warehouseRecord).completable()

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

            if (walletResponse.status === PaymentStatus.PAYMENT_APPROVED
                && warehouseResponse.status === InventoryStatus.AVAILABLE
            ) {
                println("Order created successfully")

                return OrchestratorResponseDTO(
                    requestDTO.userId,
                    requestDTO.productId,
                    requestDTO.orderId,
                    requestDTO.amount,
                    OrderStatus.ORDER_COMPLETED
                );

            }

        } catch (e: Exception) {
            println(e)

            val didWalletFail = walletReplyFuture.isCompletedExceptionally
            val didWarehouseFail = warehouseReplyFuture.isCompletedExceptionally

            if (didWalletFail && !didWarehouseFail) {
                println("Wallet service failed")

                val warehouseResponse = warehouseReplyFuture.get().value()

                if(warehouseResponse.status !== InventoryStatus.AVAILABLE){
                    println("Warehouse did not respond successfully -> nothing to roll back")
                } else {
                    println("Rolling back warehouse")
                    val warehouseRollbackRecord = ProducerRecord<String, WarehouseRequestDTO>(
                        "order-create-rollback-orchestrator-to-warehouse",
                        warehouseRequestDTO
                    )

                    println("Sending warehouse rollback request")
                    warehouseRollbackTemplate.send(warehouseRollbackRecord)
                }
            } else if (didWarehouseFail && !didWalletFail) {
                println("Warehouse service failed")

                val walletResponse = walletReplyFuture.get().value()

                if(walletResponse.status !== PaymentStatus.PAYMENT_APPROVED){
                    println("Wallet did not respond successfully -> nothing to roll back")
                } else {
                    println("Rolling back wallet")
                    val walletRollbackRecord = ProducerRecord<String, WalletRequestDTO>(
                        "order-create-rollback-orchestrator-to-wallet",
                        walletRequestDTO
                    )

                    println("Sending wallet rollback request")
                    walletRollbackTemplate.send(walletRollbackRecord)
                }
            }
            println("End of catch block")
        }

        return OrchestratorResponseDTO(
            requestDTO.userId,
            requestDTO.productId,
            requestDTO.orderId,
            requestDTO.amount,
            OrderStatus.ORDER_CANCELLED
        );
    }
}