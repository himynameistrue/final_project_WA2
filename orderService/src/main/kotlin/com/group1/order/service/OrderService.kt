package com.group1.order.service

import com.group1.dto.OrchestratorRequestDTO
import com.group1.dto.OrchestratorResponseDTO
import com.group1.order.dto.OrderRequestDTO
import com.group1.order.dto.OrderResponseDTO
import com.group1.order.entity.PurchaseOrder
import com.group1.enums.OrderStatus
import com.group1.order.repository.PurchaseOrderRepository
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class OrderService(
    val purchaseOrderRepository: PurchaseOrderRepository,
    val template: ReplyingKafkaTemplate<String, OrchestratorRequestDTO, OrchestratorResponseDTO>
) {

    fun createOrder(orderRequestDTO: OrderRequestDTO): PurchaseOrder {
        val purchaseOrder: PurchaseOrder = dtoToEntity(orderRequestDTO)

        val orchestratorRequestDTO = getOrchestratorRequestDTO(orderRequestDTO);
        val record = ProducerRecord<String, OrchestratorRequestDTO>("order-create-order-to-orchestrator", orchestratorRequestDTO)

        record.headers().add(RecordHeader(KafkaHeaders.REPLY_TOPIC, "order-create-orchestrator-to-order".toByteArray()))

        try {
            println("sending")
            val replyFuture = template.sendAndReceive(record)
            val orchestratorResponse = replyFuture.get().value()

            println("Orchestrator response is")
            println(orchestratorResponse)

            if (orchestratorResponse.status === OrderStatus.ORDER_COMPLETED) {
                purchaseOrder.status = OrderStatus.ORDER_COMPLETED
            } else {
                println("Kafka response failure")
                purchaseOrder.status = OrderStatus.ORDER_CANCELLED
            }

        } catch (e: Exception) {
            println("Kafka timeout failure")
            purchaseOrder.status = OrderStatus.ORDER_CANCELLED
        }


        return purchaseOrderRepository.save(purchaseOrder)
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
            3 to 300.0,
            4 to 100.0
        )
    }
}