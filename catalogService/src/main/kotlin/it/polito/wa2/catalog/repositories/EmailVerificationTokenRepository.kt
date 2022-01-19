package it.polito.wa2.catalog.repositories

import it.polito.wa2.catalog.domain.EmailVerificationToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmailVerificationTokenRepository: CrudRepository<EmailVerificationToken, Long> {
    fun findByToken(token: String): EmailVerificationToken?
    fun removeByToken(token: String)
    fun removeEmailVerificationTokensByExpiryDateBefore(now:Date)
}