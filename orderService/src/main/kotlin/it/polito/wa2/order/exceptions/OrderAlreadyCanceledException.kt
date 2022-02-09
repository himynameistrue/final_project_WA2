package it.polito.wa2.order.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Order is already canceled")
class OrderAlreadyCanceledException : Exception()