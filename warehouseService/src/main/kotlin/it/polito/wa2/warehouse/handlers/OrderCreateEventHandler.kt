package it.polito.wa2.warehouse.handlers

import it.polito.wa2.dto.OrderCreateWarehouseRequestDTO
import it.polito.wa2.dto.OrderCreateWarehouseResponseDTO
import it.polito.wa2.dto.OrderCreateWarehouseResponseProductDTO
import it.polito.wa2.dto.WarehouseRequestDTO
import it.polito.wa2.warehouse.services.InventoryService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.SendTo

@Configuration
class OrderCreateEventHandler(val inventoryService: InventoryService) {

    @KafkaListener(topics = ["order-create-orchestrator-to-warehouse"], groupId = "orchestrator-group")
    @SendTo
    fun consumer(
        requestDTO: OrderCreateWarehouseRequestDTO,
        @Header(KafkaHeaders.CORRELATION_ID) correlationId: String,
        @Header(KafkaHeaders.REPLY_TOPIC) replyTopic: String,
    ): OrderCreateWarehouseResponseDTO {
        println("Received request")
        println(requestDTO)
        println(correlationId)
        println(replyTopic)

        val responseItems = requestDTO.items.map {
            OrderCreateWarehouseResponseProductDTO(it.productId, it.amount, 4.2f, false, 42)
        }

        return OrderCreateWarehouseResponseDTO(true, responseItems)
        //inventoryService.deductInventory(requestDTO, correlationId, replyTopic);
    }
}