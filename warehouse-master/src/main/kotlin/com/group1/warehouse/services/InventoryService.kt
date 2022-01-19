package com.group1.warehouse.services

import com.google.gson.Gson
import com.group1.dto.WarehouseRequestDTO
import com.group1.dto.WarehouseResponseDTO
import com.group1.enums.InventoryStatus
import com.group1.warehouse.entities.WarehouseOutbox
import com.group1.warehouse.repositories.WarehouseOutboxRepository
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class InventoryService(val warehouseOutboxRepository: WarehouseOutboxRepository) {
    private var productInventoryMap: MutableMap<Int, Int>? = null

    @PostConstruct
    private fun init() {
        productInventoryMap = mutableMapOf(
            1 to 5,
            2 to 5,
            3 to 5,
            4 to 0
        )
    }

    fun deductInventory(requestDTO: WarehouseRequestDTO, correlationId: String, replyTopic: String) {
        val quantity = productInventoryMap!!.getOrDefault(requestDTO.productId, 0)

        var status = InventoryStatus.UNAVAILABLE

        if (quantity > 0) {
            status = InventoryStatus.AVAILABLE
            productInventoryMap!![requestDTO.productId] = quantity - 1
        }

        val responseDTO = WarehouseResponseDTO(
            requestDTO.orderId,
            requestDTO.userId,
            requestDTO.productId,
            status
        )

        val responseJson = Gson().toJson(responseDTO)

        val outbox = WarehouseOutbox(correlationId, replyTopic, responseDTO.javaClass.name, responseJson)

        warehouseOutboxRepository.save(outbox)
        //warehouseOutboxRepository.delete(outbox)

        return
    }

    fun addInventory(requestDTO: WarehouseRequestDTO) {
        productInventoryMap!!.computeIfPresent(requestDTO.productId) { _: Int?, v: Int -> v + 1 }
    }
}