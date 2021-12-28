package com.group1.orchestrator.handlers

import com.group1.dto.*
import com.group1.enums.InventoryStatus
import com.group1.enums.OrderStatus
import com.group1.enums.PaymentStatus
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.SendTo

@Configuration
class OrderCreatedEventHandler(
    val walletTemplate: ReplyingKafkaTemplate<String, WalletRequestDTO, WalletResponseDTO>,
    val warehouseTemplate: ReplyingKafkaTemplate<String, WarehouseRequestDTO, WarehouseResponseDTO>,
    val walletRollbackTemplate: ReplyingKafkaTemplate<String, WalletRequestDTO, Boolean>

    ) {
    lateinit var walletResponse: WalletResponseDTO

    @KafkaListener(topics = ["order-create-order-to-orchestrator"], groupId = "orchestrator-group")
    @SendTo
    fun consumer(requestDTO: OrchestratorRequestDTO): OrchestratorResponseDTO? {
        println("Received request")
        println(requestDTO)

        val walletRequestDTO = WalletRequestDTO(requestDTO.userId, requestDTO.orderId, requestDTO.amount)
        val warehouseRequestDTO = WarehouseRequestDTO(requestDTO.userId, requestDTO.productId, requestDTO.orderId)

        val walletRecord = ProducerRecord<String, WalletRequestDTO>("order-create-orchestrator-to-wallet", walletRequestDTO)
        walletRecord.headers().add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-wallet-to-orchestrator".toByteArray()))


        val warehouseRecord = ProducerRecord<String, WarehouseRequestDTO>("order-create-orchestrator-to-warehouse", warehouseRequestDTO)
        warehouseRecord.headers().add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-warehouse-to-orchestrator".toByteArray()))


        try {
            val walletReplyFuture = walletTemplate.sendAndReceive(walletRecord)
            val warehouseReplyFuture = warehouseTemplate.sendAndReceive(warehouseRecord)

            println("Sending wallet request")
            walletResponse = walletReplyFuture.get().value()
            println(walletResponse)

            println("Sending warehouse request")
            val warehouseResponse = warehouseReplyFuture.get().value()
            println(warehouseResponse)

            if (walletResponse.status === PaymentStatus.PAYMENT_APPROVED
                && warehouseResponse.status === InventoryStatus.AVAILABLE
            ) {
                return OrchestratorResponseDTO(
                    requestDTO.userId,
                    requestDTO.productId,
                    requestDTO.orderId,
                    requestDTO.amount,
                    OrderStatus.ORDER_COMPLETED
                );

            }

        } catch (e: Exception){
            println(e)
            if(::walletResponse.isInitialized){
                val walletRollbackRecord = ProducerRecord<String, WalletRequestDTO>("order-create-rollback-orchestrator-to-wallet", walletRequestDTO)
                walletRollbackRecord.headers().add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-rollback-wallet-to-orchestrator".toByteArray()))

                val walletRollbackReplyFuture = walletRollbackTemplate.sendAndReceive(walletRollbackRecord)

                println("Sending wallet rollback request")
                val walletRollbackResponse =  walletRollbackReplyFuture.get().value()
                println(walletRollbackResponse)
            }
        }

        return OrchestratorResponseDTO(requestDTO.userId, requestDTO.productId, requestDTO.orderId, requestDTO.amount, OrderStatus.ORDER_CANCELLED);
    }
}