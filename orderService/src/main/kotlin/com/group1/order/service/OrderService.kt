package com.group1.order.service

package com.vinsguru.order.service
import com.vinsguru.dto.OrchestratorRequestDTO
import com.vinsguru.dto.OrderRequestDTO
import com.vinsguru.dto.OrderResponseDTO
import com.vinsguru.enums.OrderStatus
import com.vinsguru.order.entity.PurchaseOrder
import com.vinsguru.order.repository.PurchaseOrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.FluxSink
import java.util.List
import java.util.Map
import java.util.stream.Collectors

@Service
class OrderService() {
    @Autowired
    private val purchaseOrderRepository: PurchaseOrderRepository? = null

    @Autowired
    private val sink: FluxSink<OrchestratorRequestDTO>? = null
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
        val purchaseOrder: PurchaseOrder = PurchaseOrder()
        purchaseOrder.setId(dto.getOrderId())
        purchaseOrder.setProductId(dto.getProductId())
        purchaseOrder.setUserId(dto.getUserId())
        purchaseOrder.setStatus(OrderStatus.ORDER_CREATED)
        purchaseOrder.setPrice(com.vinsguru.order.service.OrderService.Companion.PRODUCT_PRICE.get(purchaseOrder.getProductId()))
        return purchaseOrder
    }

    private fun entityToDto(purchaseOrder: PurchaseOrder): OrderResponseDTO {
        val dto: OrderResponseDTO = OrderResponseDTO()
        dto.setOrderId(purchaseOrder.getId())
        dto.setProductId(purchaseOrder.getProductId())
        dto.setUserId(purchaseOrder.getUserId())
        dto.setStatus(purchaseOrder.getStatus())
        dto.setAmount(purchaseOrder.getPrice())
        return dto
    }

    fun getOrchestratorRequestDTO(orderRequestDTO: OrderRequestDTO): OrchestratorRequestDTO {
        val requestDTO: OrchestratorRequestDTO = OrchestratorRequestDTO()
        requestDTO.setUserId(orderRequestDTO.getUserId())
        requestDTO.setAmount(com.vinsguru.order.service.OrderService.Companion.PRODUCT_PRICE.get(orderRequestDTO.getProductId()))
        requestDTO.setOrderId(orderRequestDTO.getOrderId())
        requestDTO.setProductId(orderRequestDTO.getProductId())
        return requestDTO
    }

    companion object {
        // product price map
        private val PRODUCT_PRICE: Map<Integer, Double> = Map.of(
            1, 100.0,
            2, 200.0,
            3, 300.0
        )
    }
}