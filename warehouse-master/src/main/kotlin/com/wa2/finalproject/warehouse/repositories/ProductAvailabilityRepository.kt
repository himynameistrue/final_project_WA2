package com.wa2.finalproject.warehouse.repositories

import com.wa2.finalproject.warehouse.domain.ProductAvailability
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductAvailabilityRepository: CrudRepository<ProductAvailability, Long> {
   // @Query("SELECT t FROM warehouse_product t WHERE t.product_id = :product_id")
   /* fun getWarehousesByProductID(
        @Param("product_id") productID: Long,
    ): Iterable<ProductAvailability>*/
}