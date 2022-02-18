package it.polito.wa2.warehouse.handlers

import it.polito.wa2.dto.InventoryCancelOrderRequestDTO
import it.polito.wa2.dto.InventoryCancelOrderResponseDTO
import it.polito.wa2.dto.InventoryChangeRequestDTO
import it.polito.wa2.dto.InventoryChangeResponseDTO
import it.polito.wa2.warehouse.services.InventoryService
import it.polito.wa2.warehouse.services.ProductAvailabilityService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

@Configuration
class OrderCancelEventHandler(val productAvailabilityService: ProductAvailabilityService) {

    @KafkaListener(topics = ["order-cancel-orchestrator-to-warehouse"], groupId = "orchestrator-group")
    fun cancelOrder(
            requestDTO: InventoryCancelOrderRequestDTO,
            @Header(KafkaHeaders.CORRELATION_ID) correlationId: String,
            @Header(KafkaHeaders.REPLY_TOPIC) replyTopic: String,
    ): InventoryCancelOrderResponseDTO {
        println("Received request")
        println(requestDTO)
        println(correlationId)
        println(replyTopic)

        return productAvailabilityService.cancelOrder(requestDTO, correlationId, replyTopic);
    }



    @KafkaListener(topics = ["order-create-rollback-orchestrator-to-warehouse"], groupId = "orchestrator-group")
    fun rollback(
        requestDTO: InventoryChangeResponseDTO,
        @Header(KafkaHeaders.CORRELATION_ID) correlationId: String,
        @Header(KafkaHeaders.REPLY_TOPIC) replyTopic: String,
    ) {
        println("Received request")
        println(requestDTO)
        println(correlationId)
        println(replyTopic)
    }
}