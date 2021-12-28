package com.group1.warehouse.services

import com.group1.dto.WarehouseRequestDTO
import com.group1.dto.WarehouseResponseDTO
import com.group1.enums.InventoryStatus
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class InventoryService {
    private var productInventoryMap: MutableMap<Int, Int>? = null

    @PostConstruct
    private fun init() {
        productInventoryMap = mutableMapOf(
            1 to 5,
            2 to 5,
            3 to 5,
        )
    }

    fun deductInventory(requestDTO: WarehouseRequestDTO): WarehouseResponseDTO {
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
        return responseDTO
    }

    fun addInventory(requestDTO: WarehouseRequestDTO) {
        productInventoryMap!!.computeIfPresent(requestDTO.productId) { _: Int?, v: Int -> v + 1 }
    }
}