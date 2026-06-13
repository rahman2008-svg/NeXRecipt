package com.aistudio.nexreceipt.pro.data.repository

import com.aistudio.nexreceipt.pro.data.local.ReceiptDao
import com.aistudio.nexreceipt.pro.data.model.Receipt
import kotlinx.coroutines.flow.Flow

class ReceiptRepository(private val receiptDao: ReceiptDao) {
    val allReceipts: Flow<List<Receipt>> = receiptDao.getAllReceipts()

    suspend fun getReceiptById(id: Long): Receipt? {
        return receiptDao.getReceiptById(id)
    }

    suspend fun insertReceipt(receipt: Receipt): Long {
        return receiptDao.insertReceipt(receipt)
    }

    suspend fun updateReceipt(receipt: Receipt) {
        receiptDao.updateReceipt(receipt)
    }

    suspend fun deleteReceipt(receipt: Receipt) {
        receiptDao.deleteReceipt(receipt)
    }

    suspend fun deleteReceiptById(id: Long) {
        receiptDao.deleteReceiptById(id)
    }
}
