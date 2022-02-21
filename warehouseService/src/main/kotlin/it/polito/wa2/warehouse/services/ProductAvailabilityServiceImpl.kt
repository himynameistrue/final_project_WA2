package it.polito.wa2.warehouse.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import it.polito.wa2.dto.*
import it.polito.wa2.warehouse.entities.ProductAvailability
import it.polito.wa2.warehouse.entities.ProductAvailabilityKey
import it.polito.wa2.warehouse.dto.ProductDTO
import it.polito.wa2.warehouse.dto.ProductInWarehouseDTO
import it.polito.wa2.warehouse.entities.Product
import it.polito.wa2.warehouse.entities.WarehouseOutbox
import it.polito.wa2.warehouse.repositories.ProductAvailabilityRepository
import it.polito.wa2.warehouse.repositories.ProductRepository
import it.polito.wa2.warehouse.repositories.WarehouseOutboxRepository
import it.polito.wa2.warehouse.repositories.WarehouseRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.lang.Long.min
import javax.transaction.Transactional

@Service
@Transactional
class ProductAvailabilityServiceImpl(
    val productRepository: ProductRepository,
    val warehouseRepository: WarehouseRepository,
    val availabilityRepository: ProductAvailabilityRepository,
    val warehouseOutboxRepository: WarehouseOutboxRepository

    ) : ProductAvailabilityService {

    override fun processNewOrder(requestDTO: InventoryChangeRequestDTO, correlationId:  String, replyTopic: String): InventoryChangeResponseDTO {

        lateinit var orderCreateResponse: InventoryChangeResponseDTO
        try {
            // Tirarci fuori la lista dei prodotti con disponibilità
            val quantitiesByProductId = HashMap<Long, Long>()  // mappa id, valore dei prodotti in magazzino
            val orderQuantitiesByProductId = HashMap<Long, Long>() // mappa id, quantità dei prodotti da acquistare

            requestDTO.items.forEach {
                orderQuantitiesByProductId.merge(it.productId, it.amount) { _, oldValue ->
                    oldValue + it.amount
                }
            }

            val productIds = orderQuantitiesByProductId.keys.toList() // id dei prodotti che si vogliono ordinare

            availabilityRepository.sumProductAvailabilityByProductId(productIds).forEach {
                quantitiesByProductId[it.product_id] = it.quantity
            }  // per ogni prodotto controlla e restituisce la quantità totale nei vari magazzini

            val orderProducts = HashMap<Long, Product>() // mappa id, prodotto di tutti i prodootti da ordinare

            productRepository.allByIds(productIds).forEach {
                orderProducts[it.id!!] = it
            }

            var computedTotal = 0.0f;


            orderQuantitiesByProductId.forEach {
                // Check and update grouped availability quantity
                if (!orderProducts.containsKey(it.key)) {
                    throw Exception("Specified product does not exist")
                }

                if (it.value > quantitiesByProductId.getOrDefault(it.key, 0)) {
                    throw Exception("Insufficient product quantity")
                }


                val increase = (orderProducts[it.key]!!.price!! * it.value)
                println("Increasing computedTotal by ${increase} (${orderProducts[it.key]!!.price!!} * ${it.value})")
                // Increment total amount
                computedTotal += increase
                println("computedTotal is ${computedTotal}")

            }

            val oj = ObjectMapper()
            println(oj.writeValueAsString(quantitiesByProductId))

            // Verificare che l'amount sia corretto
            if (!computedTotal.equals(requestDTO.totalPrice)) {
                throw Exception("Invalid price")
            }

            // Decrementare le quantità, verificando le soglie
            val responseProductDTOs: MutableList<InventoryChangeResponseProductDTO> = mutableListOf()

            orderQuantitiesByProductId.forEach {
                val productAvailabilitiesIterator =
                    availabilityRepository.findAllByProductIdOrderByQuantityDesc(it.key).iterator()

                var requestedQuantity = it.value
                while (requestedQuantity > 0 && productAvailabilitiesIterator.hasNext()) {
                    val productAvailability = productAvailabilitiesIterator.next()

                    val decreasedAvailability = min(requestedQuantity, productAvailability.quantity.toLong())
                    requestedQuantity -= decreasedAvailability

                    productAvailability.quantity -= decreasedAvailability.toInt()

                    availabilityRepository.save(productAvailability)

                    val responseCreateOrderWarehouse = InventoryChangeResponseProductDTO(
                            it.key,
                            productAvailability.warehouse.id!!,
                            decreasedAvailability,
                            orderProducts[it.key]!!.price!!,
                            productAvailability.quantity <= productAvailability.alarm,
                            productAvailability.quantity,
                            productAvailability.warehouse.name!!,
                            productAvailability.product.name!!
                    )

                    responseProductDTOs.add(responseCreateOrderWarehouse)
                }

            }
        orderCreateResponse = InventoryChangeResponseDTO(true, responseProductDTOs)

        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            orderCreateResponse = InventoryChangeResponseDTO(false, listOf())
        }

        val responseJson = Gson().toJson(orderCreateResponse)
        val outbox = WarehouseOutbox(correlationId, replyTopic, orderCreateResponse.javaClass.name, responseJson)

        warehouseOutboxRepository.save(outbox)
        warehouseOutboxRepository.delete(outbox)

        return orderCreateResponse
    }

    override fun rollbackOrder(requestDTO: InventoryChangeResponseDTO): InventoryChangeResponseDTO {

        try {
            // Tirarci fuori la lista dei prodotti con disponibilità
            requestDTO.items.forEach{
                updateQuantity(it.productId, it.warehouseId, (it.amount + it.remainingProducts))
                it.remainingProducts = ((it.amount + it.remainingProducts).toInt())
            }

            return requestDTO
        }

        catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            return requestDTO
        }
    }

    override fun cancelOrder(requestDTO: InventoryCancelOrderRequestDTO, correlationId: String, replyTopic: String) : InventoryCancelOrderResponseDTO{

        // get warehouse that contains product x with the minimum quantity
        requestDTO.items.forEach{
            val warehouseToFill_availability = availabilityRepository.findFirstByProductIdOrderByQuantityAsc(it.productId)
            if (warehouseToFill_availability != null) {
                updateQuantity(it.productId, warehouseToFill_availability.warehouse.id!!, (warehouseToFill_availability.quantity + it.amount))

            }
            else{
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
            }
        }
        val response = InventoryCancelOrderResponseDTO(true)

        val responseJson = Gson().toJson(response)
        val outbox = WarehouseOutbox(correlationId, replyTopic, response.javaClass.name, responseJson)

        warehouseOutboxRepository.save(outbox)
        warehouseOutboxRepository.delete(outbox)

        return response
    }

    override fun rollbackCancelOrder(requestDTO: InventoryCancelOrderRequestDTO): InventoryCancelOrderResponseDTO {

        // get warehouse that contains product x with the minimum quantity
        requestDTO.items.forEach{
            val warehouseToFill_availability = availabilityRepository.findFirstByProductIdOrderByQuantityAsc(it.productId)
            if (warehouseToFill_availability != null) {
                updateQuantity(it.productId, warehouseToFill_availability.warehouse.id!!, (warehouseToFill_availability.quantity - it.amount))

            }
            else{
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
            }
        }
        val response = InventoryCancelOrderResponseDTO(false)

        return response
    }

    override fun productInWarehouse(productId: Long, warehouseId: Long, quantity: Int, alarm: Int): ProductDTO {
        val product = productRepository.findById(productId).get()
        val warehouse = warehouseRepository.findById(warehouseId).get()
        val productInWarehouse = availabilityRepository.findAll()
        val availability =
            productInWarehouse.filter { it.product.id == productId && it.warehouse.id == warehouseId } as MutableList<ProductAvailability>
        val newProductAvailability: ProductAvailability

        if (availability.isEmpty()) { // added a new relationship
            newProductAvailability = ProductAvailability(ProductAvailabilityKey(), product, warehouse, quantity, alarm)
        } else {   // update previous relationship
            newProductAvailability = ProductAvailability(
                availability.first().id,
                availability.first().product,
                availability.first().warehouse,
                quantity,
                alarm
            )
        }

        availabilityRepository.save(newProductAvailability)
        return productRepository.findById(productId).get().toDTO()
    }

    override fun updateQuantity(productId: Long, warehouseId: Long, quantity: Long): ProductInWarehouseDTO {
        /*val productInWarehouse = availabilityRepository.findAll()
        val availability =
            productInWarehouse.filter { it.product.id == productId && it.warehouse.id == warehouseId } as MutableList<ProductAvailability>
        */
        val availability = availabilityRepository.findByProductIdAndWarehouseId(productId, warehouseId)
        var productInWarehouseDTO : ProductInWarehouseDTO
        if (availability == null) { // added a new relationship
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Relationship not found")
        }
        else {   // update previous relationship
            productInWarehouseDTO = ProductInWarehouseDTO(availability.product.id!!, availability.product.name!!, false, availability.quantity.toLong(), availability.warehouse.id!!, availability.warehouse.name!! )

            if (quantity < availability.alarm) {
                val message =
                    "Attenzione! Quantità del prodotto " + productId.toString() + " nel warehouse " + warehouseId.toString() + " sotto la soglia"
                println(message)
                //mailService.sendMessage("wa2team01@gmail.com", "prova", message)
                productInWarehouseDTO.isUnderThreshold = true
                 }
            if (quantity > 0) {
                val newProductAvailability = ProductAvailability(
                    availability.id,
                    availability.product,
                    availability.warehouse,
                    quantity.toInt(),
                    availability.alarm
                )
                availabilityRepository.save(newProductAvailability)
            }
            }
        return productInWarehouseDTO
    }
}