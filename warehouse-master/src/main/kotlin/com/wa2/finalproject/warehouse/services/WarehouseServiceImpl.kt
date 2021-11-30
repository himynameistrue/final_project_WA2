package com.wa2.finalproject.warehouse.services

import com.wa2.finalproject.warehouse.domain.ProductAvailability
import com.wa2.finalproject.warehouse.domain.ProductAvailabilityKey
import com.wa2.finalproject.warehouse.domain.Warehouse
import com.wa2.finalproject.warehouse.dto.WarehouseDTO
import com.wa2.finalproject.warehouse.repositories.ProductAvailabilityRepository
import com.wa2.finalproject.warehouse.repositories.ProductRepository
import com.wa2.finalproject.warehouse.repositories.WarehouseRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import javax.transaction.Transactional

@Service
@Transactional
class WarehouseServiceImpl(
    val warehouseRepository: WarehouseRepository,
    val productRepository: ProductRepository,
    val availabilityRepository: ProductAvailabilityRepository
) : WarehouseService {

    override fun getAll(): List<WarehouseDTO>{
        val warehouses = warehouseRepository.findAll()
        print(warehouses)
        var warehousesList = mutableListOf<WarehouseDTO>()
        for(w in warehouses){
            print(w.id.toString() + " -> "+ w.name + "| ")
            warehousesList.add(w.toDTO())
           // print(w.toDTO())
        }
        return warehousesList
    }

    override fun getById(warehouseId: Long) : WarehouseDTO{
        val warehouse = warehouseRepository.findById(warehouseId)

        if (warehouse.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found")
        }
        return warehouse.get().toDTO()
    }

    override fun create(name: String, location: String) : WarehouseDTO{
        val warehouse = Warehouse(null, name, location, mutableListOf())
        return warehouseRepository.save(warehouse).toDTO()
    }

    override fun updateFull(warehouseId: Long, name: String, location: String) : WarehouseDTO{
        var warehouse = warehouseRepository.findById(warehouseId)
        var newWarehouse: Warehouse
        if(warehouse.isEmpty){
            newWarehouse = Warehouse(null, name, location, mutableListOf())

        }
        newWarehouse = Warehouse(warehouseId, name, location)
        return warehouseRepository.save(newWarehouse).toDTO()

    }

    override fun updatePartial(warehouseId: Long, name: String?, location: String?) : WarehouseDTO {
        var warehouse = warehouseRepository.findById(warehouseId)
        var newWarehouse: Warehouse

        if(warehouse.isEmpty){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found")
        }

        if(name == null && location == null){ return warehouse.get().toDTO()}

        else if(name == null && location != null){
            newWarehouse = Warehouse(warehouseId, warehouse.get().name, location)
        }
        else if(name != null && location == null){
            newWarehouse = Warehouse(warehouseId, name, warehouse.get().location)
        }
        else{
            newWarehouse = Warehouse(warehouseId, name!!, location!!)}

        return warehouseRepository.save(newWarehouse).toDTO()

    }

    override fun delete(warehouseId: Long) {
        var warehouse = warehouseRepository.findById(warehouseId)

        if (warehouse.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found")
        }
        return warehouseRepository.delete(warehouse.get())

    }


}