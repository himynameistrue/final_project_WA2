package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents an Order after being validated by the orchestrator
 */
data class OrderCreateOrchestratorResponseDTO(
    @NotNull val buyerId: Long,
    @Valid @NotNull val items: List<InventoryChangeResponseProductDTO>,
    @NotNull val isSuccessful: Boolean
) {
    fun mapToOrderResponse(orderId: Long): OrderCreateOrderResponseDTO {
        val warehousesUnderThresholdById = mutableMapOf<Long, OrderCreateOrderResponseUnderThresholdDTO>()
        val productsById = mutableMapOf<Long, OrderCreateOrderResponseProductDTO>()
        items.forEach {

            if(it.isUnderThreshold){
                println("Evaluating for key ${it.warehouseId}")
                val warehouseDTO = warehousesUnderThresholdById.getOrPut(it.warehouseId) {
                    OrderCreateOrderResponseUnderThresholdDTO(it.warehouseId, it.warehouseName, mutableListOf())
                }

                warehouseDTO.items.add(OrderCreateOrderResponseUnderThresholdProductDTO(it.productId, it.productName, it.remainingProducts))

                warehousesUnderThresholdById[it.warehouseId] = warehouseDTO
            }

            val productDTO = productsById.getOrDefault(it.productId, OrderCreateOrderResponseProductDTO(it.productId, it.productName, 0))
            productDTO.quantity += it.amount

            productsById[it.productId] = productDTO
        }

        return OrderCreateOrderResponseDTO(orderId, isSuccessful, warehousesUnderThresholdById.values.toList(), productsById.values.toList())
    }
}