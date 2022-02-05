package it.polito.wa2.warehouse.repositories

import it.polito.wa2.warehouse.dto.ProductAvailabilityById
import it.polito.wa2.warehouse.entities.ProductAvailability
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductAvailabilityRepository : CrudRepository<ProductAvailability, Long> {
    // @Query("SELECT t FROM warehouse_product t WHERE t.product_id = :product_id")
    /* fun getWarehousesByProductID(
         @Param("product_id") productID: Long,
     ): Iterable<ProductAvailability>*/

    @Query("SELECT new it.polito.wa2.warehouse.dto.ProductAvailabilityById(t.product.id, SUM(t.quantity)) FROM ProductAvailability t WHERE t.product.id IN :productIds GROUP BY t.product.id")
    fun sumProductAvailabilityByProductId(@Param("productIds") productIds: List<Long>): Iterable<ProductAvailabilityById>

    fun findAllByProductIdOrderByQuantityDesc(productId: Long): Iterable<ProductAvailability>
}