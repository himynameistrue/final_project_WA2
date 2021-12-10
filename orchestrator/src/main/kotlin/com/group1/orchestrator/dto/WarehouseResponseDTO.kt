package com.group1.orchestrator.dto
import com.vinsguru.enums.InventoryStatus
import lombok.Data
import java.util.UUID

@Data
class InventoryResponseDTO {
    private val orderId: UUID? = null
    private val userId: Integer? = null
    private val productId: Integer? = null
    private val status: InventoryStatus? = null
}