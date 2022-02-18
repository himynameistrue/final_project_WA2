package it.polito.wa2.wallet.repositories

import it.polito.wa2.wallet.domain.Wallet
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WalletRepository: CrudRepository<Wallet, Long>{
    fun findByCustomerIdAndEnabledIsTrue(customerId: Long): Optional<Wallet>
    fun findAllByCustomerIdAndEnabledIsTrue(userId: Long): List<Wallet>
    fun findByIdAndEnabledIsTrue(id: Long): Optional<Wallet>
}