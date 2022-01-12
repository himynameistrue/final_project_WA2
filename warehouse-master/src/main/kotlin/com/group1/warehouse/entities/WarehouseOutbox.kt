package com.group1.warehouse.entities

import javax.persistence.Entity

@Entity
class WarehouseOutbox(
    val correlationId: ByteArray,
    val replyTopic: String,
    val payload: ByteArray,
) : EntityBase<Long>()

