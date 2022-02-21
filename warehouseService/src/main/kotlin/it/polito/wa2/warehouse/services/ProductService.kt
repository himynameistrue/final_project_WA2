package it.polito.wa2.warehouse.services

import it.polito.wa2.dto.ProductDTO
import it.polito.wa2.warehouse.dto.WarehouseDTO
import it.polito.wa2.dto.CommentDTO
import java.util.*

interface ProductService {

    fun getAll(category: String?): List<ProductDTO>
    fun getById(productId: Long) : ProductDTO
    fun create(name: String, description: String, picture_url: String, category: String, price: Float) : ProductDTO
    fun updateFull(productId: Long, name: String, description: String, picture_url: String, category: String, price: Float, average_rating: Float) : ProductDTO
    fun updatePartial(productId: Long, name: String?, description: String?, picture_url: String?, category: String?, price: Float?, average_rating: Float?, creation_date: Date?) : ProductDTO
    fun delete(productId: Long)
    fun getPictureByID(productId: Long): String
    fun updatePicture(productId: Long, picture_url: String): ProductDTO
    fun getWarehousesForProduct(productId: Long): List<WarehouseDTO>
    fun addComment(productId: Long, commentRequestDTO : CommentDTO): ProductDTO
}


