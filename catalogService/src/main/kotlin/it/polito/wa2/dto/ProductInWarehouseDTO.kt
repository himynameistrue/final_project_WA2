package it.polito.wa2.dto

class ProductInWarehouseDTO(
        val productId: Long,
        val productName: String,
        var isUnderThreshold: Boolean,
        val currentQuantity: Long,
        val warehouseID: Long,
        val warehouseName: String
)