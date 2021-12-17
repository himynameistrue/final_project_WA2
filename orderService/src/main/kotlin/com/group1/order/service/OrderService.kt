package com.group1.order.service

import com.group1.order.dto.OrchestratorRequestDTO
import com.group1.order.dto.OrderRequestDTO
import com.group1.order.dto.OrderResponseDTO
import com.group1.order.entity.PurchaseOrder
import com.group1.order.enums.OrderStatus
import com.group1.order.repository.PurchaseOrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.FluxSink
import java.util.stream.Collectors

@Service
class OrderService(val purchaseOrderRepository: PurchaseOrderRepository, val sink: FluxSink<OrchestratorRequestDTO>) {

    fun createOrder(orderRequestDTO: OrderRequestDTO): PurchaseOrder {
        val purchaseOrder: PurchaseOrder = purchaseOrderRepository.save(dtoToEntity(orderRequestDTO))
        sink.next(getOrchestratorRequestDTO(orderRequestDTO))
        return purchaseOrder
    }

    val all: List<Any>
        get() = purchaseOrderRepository.findAll()
            .stream()
            .map { purchaseOrder: PurchaseOrder -> entityToDto(purchaseOrder) }
            .collect(Collectors.toList())

    private fun dtoToEntity(dto: OrderRequestDTO): PurchaseOrder {
        return PurchaseOrder(
            dto.orderId!!,
            dto.userId,
            dto.productId,
            PRODUCT_PRICE[dto.productId]!!,
            OrderStatus.ORDER_CREATED
        )
    }

    private fun entityToDto(purchaseOrder: PurchaseOrder): OrderResponseDTO {
        return OrderResponseDTO(
            purchaseOrder.id,
            purchaseOrder.productId,
            purchaseOrder.userId,
            purchaseOrder.price,
            purchaseOrder.status,
        )
    }

    fun getOrchestratorRequestDTO(orderRequestDTO: OrderRequestDTO): OrchestratorRequestDTO {
        return OrchestratorRequestDTO(
            orderRequestDTO.userId,
            orderRequestDTO.productId,
            orderRequestDTO.orderId!!,
            PRODUCT_PRICE[orderRequestDTO.productId]!!
        )
    }

    companion object {
        // product price map
        private val PRODUCT_PRICE: Map<Int, Double> = mapOf(
            1 to 100.0,
            2 to 200.0,
            3 to 300.0
        )
    }
}