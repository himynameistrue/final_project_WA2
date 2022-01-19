package com.group1.warehouse.repositories

import com.group1.warehouse.entities.WarehouseOutbox
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WarehouseOutboxRepository: JpaRepository<WarehouseOutbox, UUID>{

}