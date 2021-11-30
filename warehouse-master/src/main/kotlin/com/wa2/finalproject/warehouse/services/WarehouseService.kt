package com.wa2.finalproject.warehouse.services

import com.wa2.finalproject.warehouse.dto.WarehouseDTO

interface WarehouseService {
    fun getAll(): List<WarehouseDTO>
    fun getById(warehouseId: Long) : WarehouseDTO
    fun create(name: String, location: String) : WarehouseDTO
    fun updateFull(warehouseId: Long, name: String, location: String) : WarehouseDTO
    fun updatePartial(warehouseId: Long, name: String?, location: String?) : WarehouseDTO
    fun delete(warehouseId: Long)

}