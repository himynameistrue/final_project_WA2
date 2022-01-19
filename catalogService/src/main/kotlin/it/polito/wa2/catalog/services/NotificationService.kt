package it.polito.wa2.catalog.services

import it.polito.wa2.catalog.domain.User
import it.polito.wa2.catalog.dto.EmailVerificationTokenDTO
import java.util.*

interface NotificationService {
    fun createEmailVerificationToken(user: User, expiryDate: Date, token: String): EmailVerificationTokenDTO
    fun removeEmailVerificationToken(token: String): Unit
    fun removeExpiredTokens(): Unit
}