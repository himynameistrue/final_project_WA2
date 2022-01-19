package com.group1.warehouse.handlers

import com.group1.dto.WarehouseRequestDTO
import com.group1.warehouse.services.InventoryService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

@Configuration
class OrderCreateEventHandler(val inventoryService: InventoryService) {

    @KafkaListener(topics = ["order-create-orchestrator-to-warehouse"], groupId = "orchestrator-group")
    fun consumer(
        requestDTO: WarehouseRequestDTO,
        @Header(KafkaHeaders.CORRELATION_ID) correlationId: String,
        @Header(KafkaHeaders.REPLY_TOPIC) replyTopic: String,
    ) {
        println("Received request")
        println(requestDTO)
        println(correlationId)
        println(replyTopic)

        inventoryService.deductInventory(requestDTO, correlationId, replyTopic);
    }
}