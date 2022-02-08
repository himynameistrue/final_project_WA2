package it.polito.wa2.catalog.controllers

import it.polito.wa2.catalog.exceptions.InvalidRestTemplateHostException
import it.polito.wa2.dto.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URI
import javax.servlet.http.HttpServletRequest

/**
 * ALL (without authentication)
 * list products
 * get features of products
 * get availability of products
 * OK register
 *
 * USERS with a wallet
 * order a product (check wallet and warehouse) -> createOrder
 * cancel an order (if not shipped) -> cancelOrder
 * check order status
 * retrieve orders
 * retrieve specific order
 * retrieve products
 * retrieve specific product
 * retrieve wallets
 * retrieve the transactions list
 * OK retrieve user info
 * OK update user info -> change password
 * place and evaluation of purchased product
 *
 *
 * ADMINS
 * add product
 * edit product (edit properties and picture)
 * access all information -> get product, order, wallet information
 * OK assign admin role to a customer
 * add positive transaction (recharges)
 */

@RestController
class GatewayController {

    @PostMapping("/orders/create")
    fun createOrder(request: HttpServletRequest, @RequestBody newOrderDTO: OrderCreateRequestDTO): OrderCreateResponseDTO? {
        val responseEntity = restTemplate(request, newOrderDTO, OrderCreateResponseDTO::class.java)

        return responseEntity.body
    }

    @RequestMapping("/orders/all")
    fun createOrder(request: HttpServletRequest): String? {

        val responseEntity: ResponseEntity<String> = restTemplate(request, null, String::class.java)

        return responseEntity.body
    }

    // -------------------------------------------
    // WAREHOUSE SERVICE
    // PRODUCT

    // Retrieves the list of all products. Specifying the category, retrieves all products by a given category
    @GetMapping("/products")
    fun getProducts(request: HttpServletRequest): String?{
        val responseEntity = restTemplate(request, null, String::class.java)
        // TODO error with the JSON if I use List<ProductDTO>
        return responseEntity.body
    }

    // Retrieves the product identified by productID
    @GetMapping("/products/{productID}")
    fun getProductByID(request: HttpServletRequest): ProductDTO? {
        val responseEntity = restTemplate(request, null, ProductDTO::class.java)

        // TODO propagare l'eccezione quando non trovato
        return responseEntity.body
    }

    // Gets the list of the warehouses that contain the product
    @GetMapping("/products/{productID}/warehouses")
    fun getWarehousesByProductID(request: HttpServletRequest): List<WarehouseDTO>? {
        val responseEntity = restTemplate(request, null, listOf<WarehouseDTO>()::class.java)

        // TODO if it's empty works, but if it has elements?
        return responseEntity.body
    }

    /* ---------- Only for ADMIN ---------- */

    // Add new Product
    // TODO Only for admin
    @PostMapping("/products")
    fun addProduct(request: HttpServletRequest, @RequestBody newProductDTO: ProductCreateRequestDTO): ProductDTO? {
        val responseEntity = restTemplate(request, newProductDTO, ProductDTO::class.java)

        return responseEntity.body
    }

    // Updates an existing product (full representation), or adds a new one if not exists
    // TODO Only for admin
    @PutMapping("/products/{productID}")
    fun updateFullProduct(request: HttpServletRequest, @RequestBody productFullUpdateRequestDTO: ProductFullUpdateRequestDTO): ProductDTO? {
        val responseEntity = restTemplate(request, productFullUpdateRequestDTO, ProductDTO::class.java)

        return responseEntity.body
    }

    // Updates an existing product (partial representation)
    // TODO Only for admin
    @PatchMapping("/products/{productID}")
    fun updatePartialProduct(request: HttpServletRequest, @RequestBody productPartialUpdateRequestDTO: ProductPartialUpdateRequestDTO): ProductDTO? {
        val responseEntity = restTemplate(request, productPartialUpdateRequestDTO, ProductDTO::class.java)

        return responseEntity.body
    }

    // Deletes a product
    // TODO Only for admin ??
    @DeleteMapping("/products/{productID}")
    fun deleteProduct(request: HttpServletRequest) {
        restTemplate(request, null, Void::class.java)
    }

    // Retrieves the picture of the product identified by productID
    // TODO Only for admin ??
    @GetMapping("/products/{productID}/picture")
    fun getPictureByID(request: HttpServletRequest): String? {
        val responseEntity = restTemplate(request, null, String::class.java)

        return responseEntity.body
    }

    // Updates the picture of the product identified by productID
    // TODO Only for admin
    @PostMapping("/products/{productID}/picture")
    fun updatePictureByID(request: HttpServletRequest): ProductDTO? {
        val responseEntity = restTemplate(request, null, ProductDTO::class.java)

        return responseEntity.body
    }


    // WAREHOUSE

    // Retrieves the list of all warehouses
    // TODO Only for admin ??
    @GetMapping("/warehouses")
    fun getWarehouses(request: HttpServletRequest): List<WarehouseDTO>? {
        val responseEntity = restTemplate(request, null, listOf<WarehouseDTO>()::class.java)

        return responseEntity.body
    }

    // Retrieves the warehouse identified by warehouseID
    // TODO Only for admin ??
    @GetMapping("/warehouses/{warehouseID}")
    fun getWarehouseByID(request: HttpServletRequest): WarehouseDTO? {
        val responseEntity = restTemplate(request, null, WarehouseDTO::class.java)

        return responseEntity.body
    }

    // Adds a new warehouse
    // TODO Only for admin
    @PostMapping("/warehouses")
    @ResponseStatus(HttpStatus.CREATED)
    fun createWarehouse(request: HttpServletRequest, @RequestBody warehouseCreateRequestDTO: WarehouseCreateRequestDTO): WarehouseDTO? {
        val responseEntity = restTemplate(request, warehouseCreateRequestDTO, WarehouseDTO::class.java)

        return responseEntity.body
    }

    // Updates an existing warehouse (full representation), or adds a new one if not exists
    // TODO Only for admin
    @PutMapping("/warehouses/{warehouseID}")
    fun updateFullWarehouse(request: HttpServletRequest, @RequestBody warehouseCreateRequestDTO: WarehouseCreateRequestDTO): WarehouseDTO? {
        val responseEntity = restTemplate(request, warehouseCreateRequestDTO, WarehouseDTO::class.java)

        return responseEntity.body
    }

    // Updates an existing warehouse (partial representation)
    // TODO Only for admin
    @PatchMapping("/warehouses/{warehouseID}")
    fun updatePartialWarehouse(request: HttpServletRequest, @RequestBody warehousePartialUpdateRequestDTO: ProductPartialUpdateRequestDTO): WarehouseDTO? {
        val responseEntity = restTemplate(request, warehousePartialUpdateRequestDTO, WarehouseDTO::class.java)

        return responseEntity.body
    }

    // Deletes a warehouse
    // TODO Only for admin
    @DeleteMapping("/warehouses/{warehouseID}")
    fun deleteWarehouse(request: HttpServletRequest) {
        restTemplate(request, null, Void::class.java)
    }

    // -------------------------------------------
    // WALLET SERVICE

    /*

    // Retrieves the wallet identified by walletID
    @GetMapping("/wallets/{walletID}")
    fun getWattelByID (request: HttpServletRequest): WalletDTO? {
        val responseEntity = restTemplate(request, null, WalletDTO::class.java)

        return responseEntity.body
    }

    // Creates a new wallet for a given customer
    @PostMapping("/wallets")
    fun createWallet (request: HttpServletRequest): WalletDTO? {
        // TODO take the user and send it to the walletService
        val responseEntity = restTemplate(request, null, WalletDTO::class.java)

        return responseEntity.body
    }

    // Adds a new transaction to the wallet identified by walletID
    // TODO Only for admin ??
    @PostMapping("/wallets/{walletID}/transactions")
    fun addTransaction (request: HttpServletRequest, @RequestBody transactionDTO: TransactionDTO): TransactionDTO? {
        val responseEntity = restTemplate(request, null, TransactionDTO::class.java)
    }

    // Retrieves a list of transactions regarding a given wallet in a given time frame
    @GetMapping("/wallets/{walletID}/transactions")
    fun getListTransactions (request: HttpServletRequest): List<TransactionDTO> {
        val responseEntity = restTemplate(request, null, listOf<TransactionDTO>()::class.java)

        return responseEntity.body
    }

    // Retrieves the details of a single transaction
    @GetMapping("/wallets/{walletID}/transactions/{transactionID}")
    fun getTransactionByID (request: HttpServletRequest): TransactionDTO {
        val responseEntity = restTemplate(request, null, TransactionDTO::class.java)

        return responseEntity.body
    }

    */

    // --------------------------------------------

    fun <T, V> restTemplate(
        request: HttpServletRequest,
        requestBody: V,
        responseType: Class<T>
    ): ResponseEntity<T> {
        lateinit var host: String
        var port = 8080

        with(request.requestURI) {
            when {
                startsWith("/orders") -> {
                    host = "order"
                    port = 8081
                }
                startsWith("/products") -> {
                    host = "warehouse"
                    port = 8084
                }
                else -> throw InvalidRestTemplateHostException()
            }
        }

        val uri = URI("http", null, host, port, request.requestURI, request.queryString, null)

        val httpMethod = HttpMethod.valueOf(request.method)

        return RestTemplate().exchange(
            uri,
            httpMethod,
            if(requestBody == null) null else HttpEntity<V>(requestBody),
            responseType
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleCustomException(ce: Exception): Message {
        return Message(ce.message.toString())
    }
}