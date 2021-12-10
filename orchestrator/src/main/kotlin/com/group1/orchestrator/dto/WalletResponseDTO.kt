package com.group1.orchestrator.dto
import com.vinsguru.enums.PaymentStatus
import lombok.Data
import java.util.UUID

@Data
class PaymentResponseDTO {
    private val userId: Integer? = null
    private val orderId: UUID? = null
    private val amount: Double? = null
    private val status: PaymentStatus? = null
}