package com.wa2.finalproject.warehouse.controllers

import com.wa2.finalproject.warehouse.dto.WarehouseRequestDTO
import com.wa2.finalproject.warehouse.dto.WarehouseResponseDTO
import com.wa2.finalproject.warehouse.services.InventoryService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("inventory")
class InventoryController(val service: InventoryService) {
    @PostMapping("/deduct")
    fun deduct(@RequestBody requestDTO: WarehouseRequestDTO): WarehouseResponseDTO {
        return service.deductInventory(requestDTO)
    }

    @PostMapping("/add")
    fun add(@RequestBody requestDTO: WarehouseRequestDTO) {
        service.addInventory(requestDTO)
    }
}