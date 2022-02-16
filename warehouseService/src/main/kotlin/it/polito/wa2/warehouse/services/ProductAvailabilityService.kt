package it.polito.wa2.warehouse.services

import it.polito.wa2.dto.OrderCancelWarehouseResponseDTO
import it.polito.wa2.dto.InventoryChangeRequestDTO
import it.polito.wa2.dto.InventoryChangeResponseDTO
import it.polito.wa2.warehouse.dto.ProductDTO

interface ProductAvailabilityService {
    fun processNewOrder(requestDTO: InventoryChangeRequestDTO, correlationId:  String, replyTopic: String) : InventoryChangeResponseDTO
    fun cancelOrder(requestDTO: InventoryChangeResponseDTO): OrderCancelWarehouseResponseDTO
    fun productInWarehouse(productId: Long, warehouseId: Long, quantity: Int, alarm: Int): ProductDTO
    fun updateQuantity(productId: Long, warehouseId: Long, quantity: Long): ProductDTO
}


