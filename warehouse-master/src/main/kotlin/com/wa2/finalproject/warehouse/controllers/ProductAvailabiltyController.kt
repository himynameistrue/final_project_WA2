package com.wa2.finalproject.warehouse.controllers

import com.wa2.finalproject.warehouse.dto.ProductDTO
import com.wa2.finalproject.warehouse.services.ProductAvailabilityService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/availability")
class ProductAvailabiltyController(val productAvailabilityService: ProductAvailabilityService) {

    /*Update product-warehouse relationship*/
        @PostMapping("/{productID}/warehouse/{warehouseID}") // OK
        fun newRelationship(
            @PathVariable productID: Long,
            @PathVariable warehouseID: Long,
            quantity: Int,
            alarm: Int
        ): ProductDTO {
            return productAvailabilityService.productInWarehouse(productID, warehouseID, quantity, alarm)
        }

    /*Update product-warehouse relationship*/
    @PutMapping("/{productID}/warehouse/{warehouseID}") // OK
    fun updateQuantity(
        @PathVariable productID: Long,
        @PathVariable warehouseID: Long,
        quantity: Int
    ): ProductDTO {
        return productAvailabilityService.updateQuantity(productID, warehouseID, quantity)
    }
    }
