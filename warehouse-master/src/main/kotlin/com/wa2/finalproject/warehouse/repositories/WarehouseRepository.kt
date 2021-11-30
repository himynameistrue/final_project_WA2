package com.wa2.finalproject.warehouse.repositories

import com.wa2.finalproject.warehouse.domain.Product
import com.wa2.finalproject.warehouse.domain.Warehouse
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseRepository: CrudRepository<Warehouse, Long> {

}

