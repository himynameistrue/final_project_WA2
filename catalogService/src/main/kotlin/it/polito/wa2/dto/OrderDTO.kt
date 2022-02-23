package it.polito.wa2.dto

import it.polito.wa2.enums.OrderStatus
import java.time.LocalDateTime

data class OrderDTO (val id: Long,
                     val buyer_id: Long,
                     val items: List<OrderProductDTO>,
                     val status: OrderStatus,
                     val created_at: LocalDateTime?,
                     val updated_at: LocalDateTime?
)