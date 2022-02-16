package it.polito.wa2.warehouse.handlers

import it.polito.wa2.dto.InventoryChangeRequestDTO
import it.polito.wa2.dto.InventoryChangeResponseDTO
import it.polito.wa2.warehouse.services.InventoryService
import it.polito.wa2.warehouse.services.ProductAvailabilityService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

@Configuration
class OrderCreateEventHandler(val inventoryService: InventoryService, val productAvailabilityService: ProductAvailabilityService) {

    @KafkaListener(topics = ["order-create-orchestrator-to-warehouse"], groupId = "orchestrator-group")
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

        //inventoryService.deductInventory(requestDTO, correlationId, replyTopic);
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