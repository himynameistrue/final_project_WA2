package com.group1.order.controller

import com.group1.order.dto.OrderRequestDTO
import com.group1.order.entity.PurchaseOrder
import com.group1.order.service.OrderService
import org.springframework.web.bind.annotation.*
import kotlin.collections.List;
import java.util.UUID

@RestController
@RequestMapping("orders")
class OrderController(val service: OrderService) {

    @PostMapping("/create")
    fun createOrder(@RequestBody requestDTO: OrderRequestDTO): PurchaseOrder {
        requestDTO.orderId = UUID.randomUUID()
        return service.createOrder(requestDTO)
    }

    @GetMapping("/all")
    fun list(): List<Any> {
        return service.all;
    }
}