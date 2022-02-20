package it.polito.wa2.warehouse.handlers

import it.polito.wa2.dto.InventoryChangeRequestDTO
import it.polito.wa2.dto.InventoryChangeResponseDTO
import it.polito.wa2.warehouse.services.ProductAvailabilityService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

@Configuration
class OrderCreateEventHandler(val productAvailabilityService: ProductAvailabilityService) {

    @KafkaListener(topics = ["inventory-change"], groupId = "orchestrator-group")
    fun consumer(
        requestDTO: InventoryChangeRequestDTO,
        @Header(KafkaHeaders.CORRELATION_ID) correlationId: String,
        @Header(KafkaHeaders.REPLY_TOPIC) replyTopic: String,
    ): InventoryChangeResponseDTO {
        println("Received request")
        println(requestDTO)
        println(correlationId)
        println(replyTopic)

        return productAvailabilityService.processNewOrder(requestDTO, correlationId, replyTopic);
    }



    @KafkaListener(topics = ["inventory-changed-rollback"], groupId = "orchestrator-group")
    fun rollback(
        requestDTO: InventoryChangeResponseDTO,
    ) : InventoryChangeResponseDTO{
        println("Received rollback")
        println(requestDTO)

        return productAvailabilityService.rollbackOrder(requestDTO);

    }
}