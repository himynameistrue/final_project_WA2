package com.group1.orchestrator.dto
import com.vinsguru.enums.OrderStatus
import lombok.Data
import java.util.UUID

@Data
class OrchestratorResponseDTO {
    private val userId: Integer? = null
    private val productId: Integer? = null
    private val orderId: UUID? = null
    private val amount: Double? = null
    private val status: OrderStatus? = null
}