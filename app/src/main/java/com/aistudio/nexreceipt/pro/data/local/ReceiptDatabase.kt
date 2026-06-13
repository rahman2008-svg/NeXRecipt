package com.aistudio.nexreceipt.pro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aistudio.nexreceipt.pro.data.model.Receipt

@Database(entities = [Receipt::class], version = 1, exportSchema = false)
@TypeConverters(ReceiptTypeConverters::class)
abstract class ReceiptDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao

    companion object {
        @Volatile
        private var INSTANCE: ReceiptDatabase? = null

        fun getDatabase(context: Context): ReceiptDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReceiptDatabase::class.java,
                    "receipt_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
