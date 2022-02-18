package it.polito.wa2.warehouse.services

import it.polito.wa2.warehouse.dto.CommentDTO
import it.polito.wa2.warehouse.entities.Product
import it.polito.wa2.warehouse.dto.ProductDTO
import it.polito.wa2.warehouse.dto.WarehouseDTO
import it.polito.wa2.warehouse.entities.Comment
import it.polito.wa2.warehouse.repositories.ProductAvailabilityRepository
import it.polito.wa2.warehouse.repositories.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.transaction.Transactional


@Service
@Transactional
class ProductServiceImpl(
    val productRepository: ProductRepository,
    val availabilityRepository: ProductAvailabilityRepository
) : ProductService {

    override fun getAll(category: String?): List<ProductDTO>{
        val products = productRepository.findAll()
        print(products)
        var productsList: MutableList<ProductDTO>
        if(category != null){
            productsList = products.filter { it.category == category }.map { it.toDTO() } as MutableList<ProductDTO>
        }
        else productsList = products.map { it.toDTO() } as MutableList<ProductDTO>
        /*for(p in products){
            productsList.add(p.toDTO())
            // print(w.toDTO())
        }*/
        return productsList
    }

    override fun getById(productId: Long) : ProductDTO{
        val product = productRepository.findById(productId)

        if (product.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        }
        return product.get().toDTO()
    }

    override fun create(name: String, description: String, picture_url: String, category: String, price: Float) : ProductDTO{
        val creation_date = Date()
        val product = Product(null, name, description, picture_url, category, price, 0F, creation_date, mutableListOf())
        return productRepository.save(product).toDTO()
    }

    override fun updateFull(productId: Long, name: String, description: String, picture_url: String, category: String, price: Float, average_rating: Float) : ProductDTO{
        var product = productRepository.findById(productId)
        var newProduct: Product
        val creation_date = Date()

        if(product.isEmpty){
            newProduct = Product(null, name, description, picture_url, category, price, 0F, creation_date, mutableListOf())
        }
        else{
            newProduct = Product(productId, name, description, picture_url, category, price, average_rating, creation_date , product.get().comments, product.get().availabilities)
        }
        return productRepository.save(newProduct).toDTO()
    }

    override fun updatePartial(productId: Long, name: String?, description: String?, picture_url: String?, category: String?, price: Float?, average_rating: Float?, creation_date: Date?) : ProductDTO{
        var product = productRepository.findById(productId)
        var newProduct: Product

        if(product.isEmpty){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        }

        /*if(name == null && location == null){ return product.get().toDTO()}

        else if(name == null && location != null){
            newProduct = Product(productId, product.get().name, location)
        }
        else if(name != null && location == null){
            newProduct = Product(productId, name, product.get().location)
        }
        else{
            newProduct = Product(productId, name!!, location!!)
        }*/

        newProduct = Product(productId, name ?: product.get().name, description ?: product.get().description, picture_url ?: product.get().picture_url, category ?: product.get().category, price ?: product.get().price , average_rating ?: product.get().average_rating, creation_date ?: product.get().creation_date)
        return productRepository.save(newProduct).toDTO()

    }

    override fun delete(productId: Long) {
        var product = productRepository.findById(productId)

        if (product.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        }
        availabilityRepository.deleteAllByProductId(productId)
        return productRepository.delete(product.get())

    }

    override fun getPictureByID(productId: Long): String{
        val product = productRepository.findById(productId)

        if (product.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        }
        return product.get().picture_url ?: ""
    }

    override fun updatePicture(productId: Long, picture_url: String): ProductDTO{
        var product = productRepository.findById(productId)
        var newProduct: Product = product.get()

        if (product.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        }
        newProduct = Product(newProduct.id, newProduct.name, newProduct.description, picture_url, newProduct.category, newProduct.price, newProduct.average_rating, newProduct.creation_date)
        return productRepository.save(newProduct).toDTO()
    }

    override fun getWarehousesForProduct(productId: Long): List<WarehouseDTO> {
        val product = productRepository.findById(productId)
        val warehouses = mutableListOf<WarehouseDTO>()

        if (product.isEmpty){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        }
        val availabilityById = product.get().availabilities
        for(a in availabilityById){
           warehouses.add(a.warehouse.toDTO())
        }
        return warehouses
    }

    override fun addComment(productId: Long, commentRequestDTO : CommentDTO): ProductDTO{
        var product = productRepository.findById(productId).get()
        val newComment = Comment(commentRequestDTO.title, commentRequestDTO.body, commentRequestDTO.stars)

        product.average_rating = ((product.average_rating * product.comments.size) + commentRequestDTO.stars ) / (product.comments.size +1)
        product.comments.add(newComment)

        product = productRepository.save(product)

        return product.toDTO()
    }

}