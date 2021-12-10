package com.group1.order.repository

import com.vinsguru.order.entity.PurchaseOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PurchaseOrderRepository : JpaRepository<PurchaseOrder?, UUID?>