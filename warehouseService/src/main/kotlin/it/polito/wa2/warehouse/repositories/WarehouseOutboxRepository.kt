package it.polito.wa2.warehouse.repositories

import it.polito.wa2.warehouse.entities.WarehouseOutbox
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WarehouseOutboxRepository: JpaRepository<WarehouseOutbox, Long>{

}