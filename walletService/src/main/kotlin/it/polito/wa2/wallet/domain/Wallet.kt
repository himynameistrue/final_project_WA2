package it.polito.wa2.wallet.domain

import it.polito.wa2.dto.WalletDTO
import javax.persistence.*

@Entity
class Wallet(
    var amount: Float,
    var customerId: Long,
    var enabled: Boolean = true
) : EntityBase<Long>() {
    fun toDTO(): WalletDTO {
        return WalletDTO(getId()!!, customerId, amount)
    }
}