package it.polito.wa2.warehouse.repositories

import it.polito.wa2.warehouse.entities.Product
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: CrudRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    fun allByIds(@Param("productIds") productIds: List<Long>): Iterable<Product>
}