package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents an Order after being validated by the orchestrator
 */
data class OrderCreateOrchestratorResponseDTO (@NotNull val buyerId: Long,
                                               @Valid @NotNull val items: List<InventoryChangeResponseProductDTO>,
                                               @NotNull val isSuccessful: Boolean
                                   )