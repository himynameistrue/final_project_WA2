package com.group1.warehouse.handlers

import com.group1.dto.WarehouseRequestDTO
import com.group1.dto.WarehouseResponseDTO
import com.group1.warehouse.services.InventoryService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.SendTo

@Configuration
class OrderCreateEventHandler(val inventoryService: InventoryService) {

    @KafkaListener(topics = ["order-create-orchestrator-to-warehouse"], groupId = "orchestrator-group")
    @SendTo
    fun consumer(requestDTO: WarehouseRequestDTO): WarehouseResponseDTO {
        println("Received request")
        println(requestDTO)

        return inventoryService.deductInventory(requestDTO);
    }
}