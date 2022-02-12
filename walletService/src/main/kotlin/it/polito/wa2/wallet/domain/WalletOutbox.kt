package it.polito.wa2.wallet.domain;


import javax.persistence.Column
import javax.persistence.Entity

@Entity
class WalletOutbox(
        val correlationId: String,
        val replyTopic: String,
        val payloadType: String,
        @Column(columnDefinition="text")
        val payload: String,
        ) : EntityBase<Long>()
