package it.polito.wa2.catalog.controllers

import it.polito.wa2.catalog.exceptions.InvalidRestTemplateHostException
import it.polito.wa2.dto.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URI
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

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

// TODO ?? users can also place an evaluation of the purchased product, assigning stars and leaving a comment
// TODO ?? Comments with title, body, stars and creation date can be associated to purchased products

// TODO ?? updating alarms

@RestController
class GatewayController {
    // -------------------------------------------
    // ORDER SERVICE

    /*
    // Retrieves the list of all orders
    // TODO ?? Retrieve his/her orders
    @GetMapping("/orders")
    fun getOrders (request: HttpServletRequest): List<OrderDTO>? {
        val responseEntity = restTemplate(request, null, listOf<OrderDTO>()::class.java)

        return responseEntity.body
    }

    // Retrieves the order identified by orderID
    // - Authenticated user
    @GetMapping("/orders/{orderID}")
    fun getOrderByID (request: HttpServletRequest): OrderDTO? {
        val responseEntity = restTemplate(request, null, OrderDTO::class.java)

        return responseEntity.body
    }
    */

    // Adds a new order
    // - Authenticated user
    @PostMapping("/orders")
    fun createOrder(request: HttpServletRequest, @RequestBody newOrderDTO: OrderCreateRequestDTO): OrderCreateOrderResponseDTO? {
        val responseEntity = restTemplate(request, newOrderDTO, OrderCreateOrderResponseDTO::class.java)

        // TODO SEND EMAIL if productsUnderThresholdByWarehouseId is not empty
        // Probably the warehouse name would be more useful in an email, it should be added in warehouseService

        // TODO define end user response
        return responseEntity.body
    }

    /*
    // updates the order identified by orderID
    // TODO Only for Admin ??
    @PatchMapping("/orders/{orderID}")
    fun updateOrder (request: HttpServletRequest, @RequestBody orderUpdateRequestDTO: OrderUpdateRequestDTO): OrderDTO? {
        val responseEntity = restTemplate(request, orderUpdateRequestDTO, OrderDTO::class.java)

        return responseEntity.body
    }

    // Cancels an existing order, if possible
    // - Authenticated user
    @DeleteMapping("/orders/{orderID}")
    fun deleteOrderByID (request: HttpServletRequest) {
        restTemplate(request, null, Void::class.java)
    } */

    // -------------------------------------------
    // WAREHOUSE SERVICE
    // PRODUCT

    // Retrieves the list of all products. Specifying the category, retrieves all products by a given category
    // - NO authentication
    @GetMapping("/products")
    fun getProducts(request: HttpServletRequest): String?{
        val responseEntity = restTemplate(request, null, String::class.java)
        // TODO error with the JSON if I use List<ProductDTO>
        return responseEntity.body
    }

    // Retrieves the product identified by productID
    // - NO authentication
    @GetMapping("/products/{productID}")
    fun getProductByID(request: HttpServletRequest): ProductDTO? {
        val responseEntity = restTemplate(request, null, ProductDTO::class.java)

        return responseEntity.body
    }

    // Gets the list of the warehouses that contain the product
    // - NO authentication
    @GetMapping("/products/{productID}/warehouses")
    fun getWarehousesByProductID(request: HttpServletRequest): List<WarehouseDTO>? {
        val responseEntity = restTemplate(request, null, listOf<WarehouseDTO>()::class.java)

        // TODO error with the JSON
        return responseEntity.body
    }

    /* ---------- Only for ADMIN ---------- */

    // Add new Product
    @Secured("ROLE_ADMIN")
    @PostMapping("/products")
    fun addProduct(request: HttpServletRequest, @RequestBody productCreateRequestDTO: ProductCreateRequestDTO): ProductDTO? {
        val responseEntity = restTemplate(request, productCreateRequestDTO, ProductDTO::class.java)

        return responseEntity.body
    }

    // Updates an existing product (full representation), or adds a new one if not exists
    @Secured("ROLE_ADMIN")
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
        // TODO method PATCH not allowed??
        return responseEntity.body
    }

    // Deletes a product
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/products/{productID}")
    fun deleteProduct(request: HttpServletRequest) {
        restTemplate(request, null, Void::class.java)
    }

    // Retrieves the picture of the product identified by productID
    // - NO authentication
    @GetMapping("/products/{productID}/picture")
    fun getPictureByID(request: HttpServletRequest): String? {
        val responseEntity = restTemplate(request, null, String::class.java)

        return responseEntity.body
    }

    // Updates the picture of the product identified by productID
    @Secured("ROLE_ADMIN")
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
        val responseEntity = restTemplate(request, null, mutableListOf<WarehouseDTO>()::class.java)

        // TODO error with the JSON
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
    @Secured("ROLE_ADMIN")
    @PostMapping("/warehouses")
    @ResponseStatus(HttpStatus.CREATED)
    fun createWarehouse(request: HttpServletRequest, @RequestBody warehouseCreateRequestDTO: WarehouseCreateRequestDTO): WarehouseDTO? {
        val responseEntity = restTemplate(request, warehouseCreateRequestDTO, WarehouseDTO::class.java)

        return responseEntity.body
    }

    // Updates an existing warehouse (full representation), or adds a new one if not exists
    @Secured("ROLE_ADMIN")
    @PutMapping("/warehouses/{warehouseID}")
    fun updateFullWarehouse(request: HttpServletRequest, @RequestBody warehouseCreateRequestDTO: WarehouseCreateRequestDTO): WarehouseDTO? {
        val responseEntity = restTemplate(request, warehouseCreateRequestDTO, WarehouseDTO::class.java)

        return responseEntity.body
    }

    // Updates an existing warehouse (partial representation)
    // TODO Only for admin
    @PatchMapping("/warehouses/{warehouseID}")
    fun updatePartialWarehouse(request: HttpServletRequest, @RequestBody warehousePartialUpdateRequestDTO: WarehousePartialUpdateRequestDTO): WarehouseDTO? {
        val responseEntity = restTemplate(request, warehousePartialUpdateRequestDTO, WarehouseDTO::class.java)
        // TODO method PATCH not allowed??
        return responseEntity.body
    }

    // Deletes a warehouse
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/warehouses/{warehouseID}")
    fun deleteWarehouse(request: HttpServletRequest) {
        restTemplate(request, null, Void::class.java)
    }

    // -------------------------------------------
    // WALLET SERVICE

    /*

    // Retrieves the wallet identified by walletID
    // TODO ?? Retrieve his/her wallets
    @PreAuthorize("#username == authentication.principal.username")
    @GetMapping("/wallets/{walletID}")
    fun getWattelByID (request: HttpServletRequest): WalletDTO? {
        val responseEntity = restTemplate(request, null, WalletDTO::class.java)

        return responseEntity.body
    }

    // Creates a new wallet for a given customer
    // TODO Only for admin ??
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
    - Authenticated user ??
    @GetMapping("/wallets/{walletID}/transactions")
    fun getListTransactions (request: HttpServletRequest): List<TransactionDTO> {
        val responseEntity = restTemplate(request, null, listOf<TransactionDTO>()::class.java)

        return responseEntity.body
    }

    // Retrieves the details of a single transaction
    - Authenticated user ??
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
                startsWith("/warehouses") -> {
                    host = "warehouse"
                    port = 8084
                }
                startsWith("/wallets") -> {
                    host = "wallet"
                    port = 8085
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
        return Message(ce.message!!.substringBefore("\",\"path").substringAfter("error\":\""))
    }
}