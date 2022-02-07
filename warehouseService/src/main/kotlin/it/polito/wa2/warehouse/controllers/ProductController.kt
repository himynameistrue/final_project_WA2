package it.polito.wa2.warehouse.controllers


import it.polito.wa2.dto.ProductCreateRequestDTO
import it.polito.wa2.dto.ProductFullUpdateRequestDTO
import it.polito.wa2.dto.ProductPartialUpdateRequestDTO
import it.polito.wa2.warehouse.dto.ProductDTO
import it.polito.wa2.warehouse.dto.WarehouseDTO
import it.polito.wa2.warehouse.services.ProductService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/products")
class ProductController(val productService: ProductService) {

    /*Retrieves the list of all products. Specifying the category, retrieves all products by a given category*/
    @GetMapping // OK
    // TODO category filter
    fun getProducts(category: String?): List<ProductDTO>{ // OK
        return productService.getAll(category)
    }

    /*Retrieves the product identified by productID*/
    @GetMapping("/{productID}") // OK
    fun getProductByID(@PathVariable productID: Long): ProductDTO {
        return productService.getById(productID)
    }

    /*Adds a new product*/
    @PostMapping // OK
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@RequestBody newProductDTO: ProductCreateRequestDTO): ProductDTO {
        return productService.create(newProductDTO.name, newProductDTO.description,
            newProductDTO.picture_url, newProductDTO.category, newProductDTO.price)
    }

    /*Updates an existing product (full representation), or adds a new one if not exists*/
    @PutMapping("/{productID}") // OK
    fun updateFullProduct(@PathVariable productID: Long, @RequestBody productFullUpdateRequestDTO: ProductFullUpdateRequestDTO): ProductDTO {
        return productService.updateFull(productID, productFullUpdateRequestDTO.name,
            productFullUpdateRequestDTO.description, productFullUpdateRequestDTO.picture_url, productFullUpdateRequestDTO.category,
            productFullUpdateRequestDTO.price, productFullUpdateRequestDTO.average_rating
        )
    }

    /*Updates an existing product (partial representation)*/
    @PatchMapping("/{productID}")
    fun updatePartialProduct(@PathVariable productID: Long, @RequestBody productPartialUpdateRequestDTO: ProductPartialUpdateRequestDTO): ProductDTO {
        return productService.updatePartial(productID, productPartialUpdateRequestDTO.name, productPartialUpdateRequestDTO.description,
            productPartialUpdateRequestDTO.picture_url, productPartialUpdateRequestDTO.category,
            productPartialUpdateRequestDTO.price, productPartialUpdateRequestDTO.average_rating,
            productPartialUpdateRequestDTO.creation_date)
    }

    /*Deletes a product*/
    @DeleteMapping("/{productID}") // OK
    fun deleteProduct(@PathVariable productID: Long) {
        return productService.delete(productID)
    }

    /*Retrieves the picture of the product identified by productID*/
    @GetMapping("/{productID}/picture") // OK
    fun getPictureByID(@PathVariable productID: Long): String {
        return productService.getPictureByID(productID)
    }

    /*Updates the picture of the product identified by productID*/
    @PostMapping("/{productID}/picture") // OK
    fun updatePictureByID(@PathVariable productID: Long, picture_url: String): ProductDTO {
        return productService.updatePicture(productID, picture_url)
    }

    /*Gets the list of the products that contain the product*/
    @GetMapping("/{productID}/warehouses") // OK
    fun getWarehousesByProductID(@PathVariable productID: Long): List<WarehouseDTO> {
        return productService.getWarehousesForProduct(productID)
    }
}