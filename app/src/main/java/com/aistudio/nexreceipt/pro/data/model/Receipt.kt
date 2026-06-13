package com.aistudio.nexreceipt.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "receipts")
@Serializable
data class Receipt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "RECEIPT" or "INVOICE"
    val businessName: String,
    val customerName: String,
    val receiptNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val items: List<ReceiptItem> = emptyList(),
    val taxRate: Double = 0.0,
    val discountRate: Double = 0.0,
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val subtotal: Double = 0.0,
    val totalAmount: Double = 0.0,
    val notes: String = "",
    val paymentStatus: String = "PAID", // "PAID", "UNPAID", "PENDING"
    val paymentMethod: String = "CASH", // "CASH", "CARD", "BANK TRANSFER"
    val dueDate: Long? = null
)
