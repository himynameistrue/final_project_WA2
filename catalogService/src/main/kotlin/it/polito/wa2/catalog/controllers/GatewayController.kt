package it.polito.wa2.catalog.controllers

import it.polito.wa2.catalog.exceptions.InvalidRestTemplateHostException
import it.polito.wa2.catalog.services.MailService
import it.polito.wa2.catalog.services.UserDetailsService
import it.polito.wa2.dto.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
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
class GatewayController (
        val mailService: MailService,
        val userDetailsService: UserDetailsService
    ){
    // -------------------------------------------
    // ORDER SERVICE
    // All the endpoints are only for authenticated users

    // Retrieves the list of all orders
    // If the customer is authenticated retrieve his/her list, if he's an ADMIN retrieve the list of all orders
    @GetMapping("/orders")
    fun getOrders (request: HttpServletRequest, @RequestParam("buyer_id") userID: Long?): Array<OrderDTO>? {
        val principal = (SecurityContextHolder.getContext().authentication)

        val responseEntity = if (userID != null && userDetailsService.correctID(principal.name, userID)) {
            // It's customer and can retrieve his/her order list
            restTemplate(request, null, arrayOf<OrderDTO>()::class.java)
        } else if (userDetailsService.isAdmin(principal.name)){
            // It's ADMIN retrieve all the orders
            restTemplate(request, null, arrayOf<OrderDTO>()::class.java)
        } else
            throw RuntimeException("Only the Admin can retrieve all the orders, a customer can retrieve only his/her orders")

        return responseEntity.body
    }

    // Retrieves the order identified by orderID
    @GetMapping("/orders/{orderID}")
    fun getOrderByID (request: HttpServletRequest): OrderDTO? {
        val principal = (SecurityContextHolder.getContext().authentication)

        val responseEntity = restTemplate(request, null, OrderDTO::class.java)

        if (responseEntity.hasBody()) {
            // The admin can retrieve all the orders, but a customer can retrieve only his/her orders
            if (!userDetailsService.correctID(principal.name, responseEntity.body!!.buyer_id) &&
                !userDetailsService.isAdmin(principal.name)
            )
                throw RuntimeException("Normal customers can't retrieve other users orders")
        }
        return responseEntity.body
    }

    // Adds a new order
    @Secured("ROLE_CUSTOMER")
    @PostMapping("/orders")
    fun createOrder(request: HttpServletRequest, @RequestBody orderRequestDTO: IncompleteOrderCreateRequestDTO): OrderCreateOrderResponseDTO? {
        val principal = (SecurityContextHolder.getContext().authentication)
        val userID = userDetailsService.getIdFromEmail(principal.name)
        val newOrderDTO = userID?.let { OrderCreateRequestDTO(it, orderRequestDTO.totalPrice, orderRequestDTO.items) }

        val responseEntity = restTemplate(request, newOrderDTO, OrderCreateOrderResponseDTO::class.java)

        val responseBody = responseEntity.body

        if (responseBody!!.warehousesUnderThresholdById.isNotEmpty()) {
            responseBody.warehousesUnderThresholdById.forEach{ (idW, nameW, listProduct) ->
                var message = "Attention! These products are under threshold:\n"
                listProduct.forEach { prodDTO ->
                    message += "Product " + prodDTO.productId + ": " + prodDTO.productName + " in the warehouse " + idW + ": " + nameW +
                            " - remaining quantity: " + prodDTO.remainingProducts + "\n"
                }
                userDetailsService.getAdminsEmail().forEach { email ->
                    mailService.sendMessage(email, "Products under threshold", message)
                }
            }
        }
        if (responseBody.isSuccessful) {
            mailService.sendMessage(principal.name, "Order confirmed", "Your order is confirmed!\nOrder ID: " + responseBody.id +
            "You will receive and email every time your order is updated\nThank you for your purchase")
        }

        return responseBody
    }


    // updates the order identified by orderID
    @Secured("ROLE_ADMIN")
    @PatchMapping("/orders/{orderID}")
    fun updateOrder (request: HttpServletRequest, orderUpdateRequestDTO: OrderUpdateRequestDTO): OrderDTO? {
        val responseEntity = restTemplate(request, orderUpdateRequestDTO, OrderDTO::class.java)

        return responseEntity.body
    }

    // Cancels an existing order, if possible
    // - Authenticated user
    @DeleteMapping("/orders/{orderID}")
    fun deleteOrderByID (request: HttpServletRequest) {
        val principal = (SecurityContextHolder.getContext().authentication)

        if (!userDetailsService.isAdmin(principal.name)) {
            // It's a Customer, retrieve it's ID and sent it to the order service
            val userID = userDetailsService.getIdFromEmail(principal.name)
            val uri = URI("http", null, "order", 8081, request.requestURI, "buyer_id=$userID", null)

            RestTemplate().exchange(uri, HttpMethod.valueOf(request.method), null, Void::class.java)
        } else {
            restTemplate(request, null, Void::class.java)
        }
    }

    // -------------------------------------------
    // WAREHOUSE SERVICE
    // PRODUCT

    // Retrieves the list of all products. Specifying the category, retrieves all products by a given category
    // - NO authentication
    @GetMapping("/products")
    fun getProducts(request: HttpServletRequest): Array<ProductDTO>? {
        val responseEntity = restTemplate(request, null, arrayOf<ProductDTO>()::class.java)

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
    fun getWarehousesByProductID(request: HttpServletRequest): Array<WarehouseDTO>? {
        val responseEntity = restTemplate(request, null, arrayOf<WarehouseDTO>()::class.java)

        return responseEntity.body
    }

    // Retrieves the picture of the product identified by productID
    // - NO authentication
    @GetMapping("/products/{productID}/picture")
    fun getPictureByID(request: HttpServletRequest): String? {
        val responseEntity = restTemplate(request, null, String::class.java)

        return responseEntity.body
    }

    // Add a comment about a product
    // TODO ?? must check that the product was bought from this user?
    @Secured("ROLE_CUSTOMER")
    @PutMapping("/products/{productID}/comments")
    fun addComment(request: HttpServletRequest, @RequestBody commentDTO : CommentDTO): ProductDTO? {
        val responseEntity = restTemplate(request, commentDTO, ProductDTO::class.java)

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
    @Secured("ROLE_ADMIN")
    @PatchMapping("/products/{productID}")
    fun updatePartialProduct(request: HttpServletRequest, @RequestBody productPartialUpdateRequestDTO: ProductPartialUpdateRequestDTO): ProductDTO? {
        val responseEntity = restTemplate(request, productPartialUpdateRequestDTO, ProductDTO::class.java)

        return responseEntity.body
    }

    // Deletes a product
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/products/{productID}")
    fun deleteProduct(request: HttpServletRequest) {
        restTemplate(request, null, Void::class.java)
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
    @Secured("ROLE_ADMIN")
    @GetMapping("/warehouses")
    fun getWarehouses(request: HttpServletRequest): Array<WarehouseDTO>? {
        val responseEntity = restTemplate(request, null, arrayOf<WarehouseDTO>()::class.java)

        return responseEntity.body
    }

    // Retrieves the warehouse identified by warehouseID
    @Secured("ROLE_ADMIN")
    @GetMapping("/warehouses/{warehouseID}")
    fun getWarehouseByID(request: HttpServletRequest): WarehouseDTO? {
        val responseEntity = restTemplate(request, null, WarehouseDTO::class.java)

        return responseEntity.body
    }

    // Adds a new warehouse
    @Secured("ROLE_ADMIN")
    @PostMapping("/warehouses")
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
    @Secured("ROLE_ADMIN")
    @PatchMapping("/warehouses/{warehouseID}")
    fun updatePartialWarehouse(request: HttpServletRequest, @RequestBody warehousePartialUpdateRequestDTO: WarehousePartialUpdateRequestDTO): WarehouseDTO? {
        val responseEntity = restTemplate(request, warehousePartialUpdateRequestDTO, WarehouseDTO::class.java)

        return responseEntity.body
    }

    // Deletes a warehouse
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/warehouses/{warehouseID}")
    fun deleteWarehouse(request: HttpServletRequest) {
        restTemplate(request, null, Void::class.java)
    }


    // Update the availability of a product and the alarm threshold
    @Secured("ROLE_ADMIN")
    @PostMapping("/availability/{productID}/warehouse/{warehouseID}")
    fun newRelationship(request: HttpServletRequest, @RequestBody productAvailabilityUpdateRequestDTO: ProductAvailabilityUpdateRequestDTO): ProductDTO? {
        val responseEntity = restTemplate(request, productAvailabilityUpdateRequestDTO, ProductDTO::class.java)

        return responseEntity.body
    }

    // Update the quantity of a product
    @Secured("ROLE_ADMIN")
    @PutMapping("/availability/{productID}/warehouse/{warehouseID}")
    fun updateQuantity(request: HttpServletRequest): ProductInWarehouseDTO? {
        val responseEntity = restTemplate(request, null, ProductInWarehouseDTO::class.java)

        return responseEntity.body
    }


    // -------------------------------------------
    // WALLET SERVICE

    // Retrieve the list of the wallets of a buyer ID
    @GetMapping("/wallets")
    fun getListOfWalletByUserId(request: HttpServletRequest, @RequestParam userId: Long): Array<WalletDTO>? {
        val principal = (SecurityContextHolder.getContext().authentication)

        val responseEntity = if (userDetailsService.correctID(principal.name, userId) ||
            userDetailsService.isAdmin(principal.name)) {
            // It's correct customer or an admin and can retrieve the wallets
            restTemplate(request, null, arrayOf<WalletDTO>()::class.java)
        } else
            throw RuntimeException("Only the Admin can retrieve all the wallets, a customer can retrieve only his/her wallets")

        return responseEntity.body
    }

    // Retrieves the wallet identified by walletID
    @GetMapping("/wallets/{walletID}")
    fun getWalletByID (request: HttpServletRequest): WalletDTO? {
        val principal = (SecurityContextHolder.getContext().authentication)

        val responseEntity = restTemplate(request, null, WalletDTO::class.java)

        if (responseEntity.hasBody()) {
            // The admin can retrieve all the wallets, but a customer can retrieve only his/her wallet
            if (!userDetailsService.correctID(principal.name, responseEntity.body!!.customerId) &&
                !userDetailsService.isAdmin(principal.name))
                throw RuntimeException("Normal customers can't retrieve other users wallets")
        }
        return responseEntity.body
    }

    // Creates a new wallet for a given customer
    @Secured("ROLE_ADMIN")
    @PostMapping("/wallets")
    fun createWallet (request: HttpServletRequest, @RequestBody customerData: Map<String, String>): WalletDTO? {
        val principal = (SecurityContextHolder.getContext().authentication)

        if (!userDetailsService.isCustomer(principal.name)) {
            throw RuntimeException("Only a CUSTOMER can have a wallet!")
        }
        val responseEntity = restTemplate(request, customerData, WalletDTO::class.java)

        return responseEntity.body
    }

    // Disable the wallet for a given customer
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{customerID}")
    fun disableWallet(request: HttpServletRequest): WalletDTO? {
        val responseEntity = restTemplate(request, null, WalletDTO::class.java)

        return responseEntity.body
    }

    // Adds a new transaction to the wallet identified by walletID
    @PostMapping("/wallets/{walletID}/transactions")
    fun addTransaction (request: HttpServletRequest, @RequestBody requestDTO: WalletRequestDTO): WalletResponseDTO? {
        val principal = (SecurityContextHolder.getContext().authentication)

        val responseEntity = if (userDetailsService.correctID(principal.name, requestDTO.userId)) {
            // It's customer and can make the transaction on his/her wallet
            restTemplate(request, requestDTO, WalletResponseDTO::class.java)
        } else if (userDetailsService.isAdmin(principal.name)){
            // It's ADMIN can make everything
            restTemplate(request, requestDTO, WalletResponseDTO::class.java)
        } else
            throw RuntimeException("A customer can make transaction only on his/her wallet")

        return responseEntity.body
    }

    // Retrieves a list of transactions regarding a given wallet in a given time frame
    @Secured("ROLE_ADMIN")
    @GetMapping("/wallets/{walletID}/transactions")
    fun getListTransactions (request: HttpServletRequest): List<TransactionDTO>? {
        val responseEntity = restTemplate(request, null, listOf<TransactionDTO>()::class.java)

        return responseEntity.body
    }

    // Retrieves the details of a single transaction
    @GetMapping("/wallets/{walletID}/transactions/{transactionID}")
    fun getTransactionByID (request: HttpServletRequest): TransactionDTO? {
        val principal = (SecurityContextHolder.getContext().authentication)

        val responseEntity = restTemplate(request, null, TransactionDTO::class.java)

        if (responseEntity.hasBody()) {
            // The admin can retrieve all the wallets, but a customer can retrieve only his/her wallet
            if (!userDetailsService.correctID(principal.name, responseEntity.body!!.customerId) &&
                !userDetailsService.isAdmin(principal.name))
                throw RuntimeException("Normal customers can't retrieve other users transactions")
        }
        return responseEntity.body
    }


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
                startsWith("/availability") -> {
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

        val restTemplate = RestTemplate()
        restTemplate.requestFactory = HttpComponentsClientHttpRequestFactory()

        return restTemplate.exchange(
            uri,
            httpMethod,
            if(requestBody == null) null else HttpEntity<V>(requestBody),
            responseType
        )
    }

    fun createWallet(userEmail: String) {
        val customerId = userDetailsService.getIdFromEmail(userEmail).toString()
        val uri = URI("http", null, "wallet", 8085, "/wallets", null, null)

        val responseEntity = RestTemplate().exchange(
            uri,
            HttpMethod.POST,
            HttpEntity<Map<String, String>>(mapOf<String, String>(Pair<String, String>("customerId", customerId))),
            WalletDTO::class.java
        )

        // TODO posso essere certa che è stato creato o devo controllare?
        responseEntity.body
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleCustomException(ce: Exception): Message {
        return Message(ce.message!!.substringBefore("\",\"path").substringAfter("error\":\""))
    }
}