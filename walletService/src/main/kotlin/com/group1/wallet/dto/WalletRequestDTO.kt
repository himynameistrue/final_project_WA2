package com.group1.wallet.dto

import java.util.UUID

data class WalletRequestDTO (
    val userId: Int,
    val orderId: UUID,
    val amount: Double,
)