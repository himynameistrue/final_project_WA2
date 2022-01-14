package com.group1.warehouse.entities

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class WarehouseOutbox(
    val correlationId: ByteArray,
    val replyTopic: String,
    val payloadType: String,
    @Column(columnDefinition="text")
    val payload: String,
) : EntityBase<Long>()

