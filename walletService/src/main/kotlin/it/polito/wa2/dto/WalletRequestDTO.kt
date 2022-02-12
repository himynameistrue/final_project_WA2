package it.polito.wa2.dto

import java.util.UUID

data class WalletRequestDTO (
    val userId: Long,
    val orderId: Long,
    val amount: Float,
    val transationId : Long? = null
)