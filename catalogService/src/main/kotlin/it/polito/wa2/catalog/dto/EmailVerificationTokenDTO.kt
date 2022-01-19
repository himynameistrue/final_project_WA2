package it.polito.wa2.catalog.dto

import java.util.*

data class EmailVerificationTokenDTO (
    var id: Long?,
    var email: String,
    var expiryDate: Date,
    var token: String
)