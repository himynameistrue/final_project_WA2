package it.polito.wa2.dto

import org.springframework.data.util.ProxyUtils

data class WarehouseDTO(
    val id: Long,
    val name: String,
    val location: String,
    val availabilities: Map<Long, Int>
)