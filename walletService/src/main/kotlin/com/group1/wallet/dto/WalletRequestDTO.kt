package com.group1.wallet.dto

import lombok.Data
import java.util.UUID

@Data
class PaymentRequestDTO {
    private val userId: Integer? = null
    private val orderId: UUID? = null
    private val amount: Double? = null
}