package it.polito.wa2.warehouse.handlers

import it.polito.wa2.dto.InventoryCancelOrderRequestDTO
import it.polito.wa2.dto.InventoryCancelOrderResponseDTO
import it.polito.wa2.dto.InventoryChangeResponseDTO
import it.polito.wa2.warehouse.services.ProductAvailabilityService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

@Configuration
class OrderCancelEventHandler(val productAvailabilityService: ProductAvailabilityService) {

    @KafkaListener(topics = ["inventory-return"], groupId = "orchestrator-group")
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



    @KafkaListener(topics = ["inventory-returned-rollback"], groupId = "orchestrator-group")
    fun rollback(
        requestDTO: InventoryChangeResponseDTO,
    ) {
        println("Received request")
        println(requestDTO)
    }
}