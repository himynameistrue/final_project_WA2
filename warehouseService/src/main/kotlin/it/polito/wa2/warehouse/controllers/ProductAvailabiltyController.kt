package it.polito.wa2.warehouse.controllers

import it.polito.wa2.dto.ProductAvailabilityUpdateRequestDTO
import it.polito.wa2.dto.ProductDTO
import it.polito.wa2.warehouse.dto.ProductInWarehouseDTO
import it.polito.wa2.warehouse.services.ProductAvailabilityService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/availability")
class ProductAvailabiltyController(val productAvailabilityService: ProductAvailabilityService) {

    /*Update product-warehouse relationship*/
        @PostMapping("/{productID}/warehouse/{warehouseID}") // OK
        fun newRelationship(
            @PathVariable productID: Long,
            @PathVariable warehouseID: Long,
            @RequestBody productAvailabilityUpdateRequestDTO: ProductAvailabilityUpdateRequestDTO
        ): ProductDTO {
            return productAvailabilityService.productInWarehouse(productID, warehouseID,
                productAvailabilityUpdateRequestDTO.quantity, productAvailabilityUpdateRequestDTO.alarm)
        }

    /*Update product-warehouse relationship*/
    @PutMapping("/{productID}/warehouse/{warehouseID}") // OK
    fun updateQuantity(
        @PathVariable productID: Long,
        @PathVariable warehouseID: Long,
        quantity: Long
    ): ProductInWarehouseDTO {
        return productAvailabilityService.updateQuantity(productID, warehouseID, quantity)
    }



}


