package polito.wa2.team1.orderservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Order cannot be canceled")
class OrderStatusChangeFailedException : Exception()