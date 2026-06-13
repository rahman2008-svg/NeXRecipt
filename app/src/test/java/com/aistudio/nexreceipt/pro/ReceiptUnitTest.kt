package com.aistudio.nexreceipt.pro

import com.aistudio.nexreceipt.pro.data.model.Receipt
import com.aistudio.nexreceipt.pro.data.model.ReceiptItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ReceiptUnitTest {

    @Test
    fun testReceiptItemTotalCalculation() {
        val item = ReceiptItem(name = "Premium Subscription", quantity = 3, unitPrice = 14.99)
        // 3 * 14.99 = 44.97
        assertEquals(44.97, item.total, 0.001)
    }

    @Test
    fun testReceiptCalculations() {
        val items = listOf(
            ReceiptItem("Web Design Service", 1, 500.0),
            ReceiptItem("API Integration", 2, 150.0)
        )

        val subtotal = items.sumOf { it.total } // 500.0 + 300.0 = 800.0
        val discountRate = 10.0 // 10% discount
        val discAmt = subtotal * (discountRate / 100.0) // 80.0
        val taxRate = 12.0 // 12% tax
        val taxAmt = (subtotal - discAmt) * (taxRate / 100.0) // (800.0 - 80.0) * 0.12 = 720.0 * 0.12 = 86.4
        val total = subtotal - discAmt + taxAmt // 800.0 - 80.0 + 86.4 = 806.4

        val receipt = Receipt(
            type = "INVOICE",
            businessName = "NexVora Lab's Ofc",
            customerName = "Prince AR Abdur Rahman",
            receiptNumber = "INV-001",
            items = items,
            taxRate = taxRate,
            discountRate = discountRate,
            taxAmount = taxAmt,
            discountAmount = discAmt,
            subtotal = subtotal,
            totalAmount = total
        )

        assertEquals(800.0, receipt.subtotal, 0.001)
        assertEquals(80.0, receipt.discountAmount, 0.001)
        assertEquals(86.4, receipt.taxAmount, 0.001)
        assertEquals(806.4, receipt.totalAmount, 0.001)
    }
}
