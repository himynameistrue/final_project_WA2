package it.polito.wa2.dto

import it.polito.wa2.enums.OrderStatus

data class OrderCreateResponseDTO (val id: Long,
                     val buyer_id: Long,
                     val items: List<OrderProductDTO>,
                     val status: OrderStatus)