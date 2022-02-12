package it.polito.wa2.wallet.domain

import it.polito.wa2.dto.TransactionDTO
import java.util.*
import javax.persistence.*

@Entity
class Transaction(
    var timestamp: Date,
    var amount: Float,

    @ManyToOne
     @JoinColumn(name="customer_wallet_id", referencedColumnName="id")
     var customer: Wallet,
) : EntityBase<Long>(){
     fun toDTO(): TransactionDTO {
          return TransactionDTO(getId()!!, customer.getId()!!, amount, timestamp)
     }
}