package com.wa2.finalproject.warehouse.services

import com.wa2.finalproject.warehouse.dto.ProductDTO
import com.wa2.finalproject.warehouse.dto.WarehouseDTO
import java.util.*

interface ProductAvailabilityService {

    fun productInWarehouse(productId: Long, warehouseId: Long, quantity: Int, alarm: Int): ProductDTO
    fun updateQuantity(productId: Long, warehouseId: Long, quantity: Int): ProductDTO
}


