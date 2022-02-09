package it.polito.wa2.order.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Order creation failed")
class OrderCreationFailedException : Exception()