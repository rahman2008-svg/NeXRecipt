package com.aistudio.nexreceipt.pro.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptItem(
    val name: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val total: Double
        get() = quantity * unitPrice
}
