package it.polito.wa2.warehouse.repositories

import it.polito.wa2.warehouse.entities.Warehouse
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseRepository: CrudRepository<Warehouse, Long> {
}

