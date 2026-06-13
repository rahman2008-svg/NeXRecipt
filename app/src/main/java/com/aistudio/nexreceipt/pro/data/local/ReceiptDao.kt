package com.aistudio.nexreceipt.pro.data.local

import androidx.room.*
import com.aistudio.nexreceipt.pro.data.model.Receipt
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts ORDER BY timestamp DESC")
    fun getAllReceipts(): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getReceiptById(id: Long): Receipt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt): Long

    @Update
    suspend fun updateReceipt(receipt: Receipt)

    @Delete
    suspend fun deleteReceipt(receipt: Receipt)

    @Query("DELETE FROM receipts WHERE id = :id")
    suspend fun deleteReceiptById(id: Long)
}
