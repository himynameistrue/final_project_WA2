package com.wa2.finalproject.warehouse.services

import com.wa2.finalproject.warehouse.domain.ProductAvailability
import com.wa2.finalproject.warehouse.domain.ProductAvailabilityKey
import com.wa2.finalproject.warehouse.dto.ProductDTO
import com.wa2.finalproject.warehouse.repositories.ProductAvailabilityRepository
import com.wa2.finalproject.warehouse.repositories.ProductRepository
import com.wa2.finalproject.warehouse.repositories.WarehouseRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import javax.transaction.Transactional

@Service
@Transactional
class ProductAvailabilityServiceImpl(
    val productRepository: ProductRepository,
    val warehouseRepository: WarehouseRepository,
    val availabilityRepository: ProductAvailabilityRepository,
    val mailService: MailService

    ) : ProductAvailabilityService {

    override fun productInWarehouse(productId: Long, warehouseId: Long, quantity: Int, alarm: Int): ProductDTO{
        val product = productRepository.findById(productId).get()
        val warehouse = warehouseRepository.findById(warehouseId).get()
        val productInWarehouse = availabilityRepository.findAll()
        val availability =  productInWarehouse.filter { it.product.id == productId && it.warehouse.id == warehouseId} as MutableList<ProductAvailability>
        val newProductAvailability : ProductAvailability

        if(availability.isEmpty()){ // added a new relationship
            newProductAvailability = ProductAvailability(ProductAvailabilityKey(), product, warehouse, quantity, alarm)
        }
        else{   // update previous relationship
            newProductAvailability = ProductAvailability(availability.first().id, availability.first().product, availability.first().warehouse, quantity, alarm)
        }

        availabilityRepository.save(newProductAvailability)
        return product.toDTO()
    }

    override fun updateQuantity(productId: Long, warehouseId: Long, quantity: Int): ProductDTO {
        val productInWarehouse = availabilityRepository.findAll()
        val availability =  productInWarehouse.filter { it.product.id == productId && it.warehouse.id == warehouseId} as MutableList<ProductAvailability>

        if(availability.isEmpty()){ // added a new relationship
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Relationship not found")
        }
        else{   // update previous relationship
            if (quantity < availability.first().alarm){
                val message = "Attenzione! Quantità del prodotto " + productId.toString() + " nel warehouse " + warehouseId.toString() + " sotto la soglia"
                mailService.sendMessage("wa2team01@gmail.com", "prova", message)
            }
            if(quantity > 0){
               val newProductAvailability = ProductAvailability(availability.first().id, availability.first().product, availability.first().warehouse, quantity, availability.first().alarm)
                availabilityRepository.save(newProductAvailability)
            }
        }


        return availability.first().product.toDTO()
    }

}