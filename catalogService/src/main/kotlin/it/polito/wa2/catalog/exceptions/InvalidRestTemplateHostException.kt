package it.polito.wa2.catalog.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "This route should not use a Rest Template")
class InvalidRestTemplateHostException : Exception()