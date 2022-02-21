package it.polito.wa2.warehouse.services

import it.polito.wa2.dto.*
import it.polito.wa2.warehouse.dto.ProductDTO
import it.polito.wa2.warehouse.dto.ProductInWarehouseDTO

interface ProductAvailabilityService {
    fun processNewOrder(requestDTO: InventoryChangeRequestDTO, correlationId:  String, replyTopic: String) : InventoryChangeResponseDTO
    fun rollbackOrder(requestDTO: InventoryChangeResponseDTO): InventoryChangeResponseDTO
    fun cancelOrder(requestDTO: InventoryCancelOrderRequestDTO, correlationId: String, replyTopic: String) : InventoryCancelOrderResponseDTO
    fun rollbackCancelOrder(requestDTO: InventoryCancelOrderRequestDTO): InventoryCancelOrderResponseDTO
    fun productInWarehouse(productId: Long, warehouseId: Long, quantity: Int, alarm: Int): ProductDTO
    fun updateQuantity(productId: Long, warehouseId: Long, quantity: Long): ProductInWarehouseDTO
}

