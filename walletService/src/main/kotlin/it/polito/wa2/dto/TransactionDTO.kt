package it.polito.wa2.dto

import java.util.*

data class TransactionDTO(val id: Long,
                          val orderId: Long,
                          val customerId: Long,
                          val amount: Float,
                          val timestamp: Date?=null)
