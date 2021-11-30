package com.wa2.finalproject.warehouse.repositories

import com.wa2.finalproject.warehouse.domain.Product
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: CrudRepository<Product, Long>