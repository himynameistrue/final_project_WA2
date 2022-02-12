package it.polito.wa2.warehouse.services

import it.polito.wa2.dto.OrderCancelWarehouseResponseDTO
import it.polito.wa2.dto.OrderCreateWarehouseRequestDTO
import it.polito.wa2.dto.OrderCreateWarehouseResponseDTO
import it.polito.wa2.warehouse.dto.ProductDTO
import it.polito.wa2.warehouse.dto.WarehouseDTO
import java.util.*

interface ProductAvailabilityService {
    fun processNewOrder(requestDTO: OrderCreateWarehouseRequestDTO, correlationId:  String, replyTopic: String) : OrderCreateWarehouseResponseDTO
    fun cancelOrder(requestDTO: OrderCreateWarehouseResponseDTO): OrderCancelWarehouseResponseDTO
    fun productInWarehouse(productId: Long, warehouseId: Long, quantity: Int, alarm: Int): ProductDTO
    fun updateQuantity(productId: Long, warehouseId: Long, quantity: Long): ProductDTO
}

