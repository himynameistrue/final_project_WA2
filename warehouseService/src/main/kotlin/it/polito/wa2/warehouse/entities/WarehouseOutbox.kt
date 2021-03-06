package it.polito.wa2.warehouse.entities

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class WarehouseOutbox(
    val correlationId: String,
    val replyTopic: String,
    val payloadType: String,
    @Column(columnDefinition="text")
    val payload: String,
) : EntityBase<Long>()

