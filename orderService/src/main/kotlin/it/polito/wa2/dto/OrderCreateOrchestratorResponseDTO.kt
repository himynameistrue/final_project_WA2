package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents an Order after being validated by the orchestrator
 */
data class OrderCreateOrchestratorResponseDTO(
    @NotNull val buyerId: Long,
    @Valid @NotNull val items: List<OrderCreateWarehouseResponseProductDTO>,
    @NotNull val isSuccessful: Boolean
) {
    fun mapToOrderResponse(): OrderCreateOrderResponseDTO {
        val productsUnderThresholdByWarehouseId = mutableMapOf<Long, MutableList<OrderCreateOrderResponseProductDTO>>()
        items.forEach {
            if(it.isUnderThreshold){
                println("Evaluating for key ${it.warehouseId}")
                productsUnderThresholdByWarehouseId.getOrPut(it.warehouseId) { mutableListOf() }
                    .add(OrderCreateOrderResponseProductDTO(it.productId, it.remainingProducts))
            }
        }

        return OrderCreateOrderResponseDTO(isSuccessful, productsUnderThresholdByWarehouseId)
    }
}