package com.group1.order.controller

import com.vinsguru.dto.OrderRequestDTO
import com.vinsguru.dto.OrderResponseDTO
import com.vinsguru.order.entity.PurchaseOrder
import com.vinsguru.order.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.List
import java.util.UUID

@RestController
@RequestMapping("order")
class OrderController {
    @Autowired
    private val service: OrderService? = null
    @PostMapping("/create")
    fun createOrder(@RequestBody requestDTO: OrderRequestDTO): PurchaseOrder {
        requestDTO.setOrderId(UUID.randomUUID())
        return service.createOrder(requestDTO)
    }

    @get:GetMapping("/all")
    val orders: List<Any>
        get() = service.getAll()
}