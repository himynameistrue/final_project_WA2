package it.polito.wa2.warehouse.controllers

import it.polito.wa2.warehouse.dto.WarehouseDTO
import it.polito.wa2.warehouse.services.WarehouseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/warehouses")
class WarehouseController(val warehouseService: WarehouseService) {
    /* Retrieves the list of all warehouses */

    @GetMapping
    fun getWarehouses(): List<WarehouseDTO>{ // OK
        return warehouseService.getAll()
    }

    /*Retrieves the warehouse identified by warehouseID*/
    @GetMapping("/{warehouseID}") // OK
    fun getWarehouseByID(@PathVariable warehouseID: Long): WarehouseDTO {
        return warehouseService.getById(warehouseID)
    }

    /*Adds a new warehouse*/
    @PostMapping("/") // OK
    @ResponseStatus(HttpStatus.CREATED)
    fun createWarehouse(name: String, location: String): WarehouseDTO {
        return warehouseService.create(name, location)
    }

    /*Updates an existing warehouse (full representation), or adds a new one if not exists*/
    @PutMapping("/{warehouseID}") // OK
    fun updateFullWarehouse(@PathVariable warehouseID: Long, name: String, location: String): WarehouseDTO {
        return warehouseService.updateFull(warehouseID, name, location)
    }

    /*Updates an existing warehouse (partial representation)*/
    @PatchMapping("/{warehouseID}") // OK
    fun updatePartialWarehouse(@PathVariable warehouseID: Long, name: String?, location: String?): WarehouseDTO {
        return warehouseService.updatePartial(warehouseID, name, location)
    }

    /*Deletes a warehouse*/
    @DeleteMapping("/{warehouseID}") // OK
    fun deleteWarehouse(@PathVariable warehouseID: Long) {
        return warehouseService.delete(warehouseID)
    }
}
