package com.aistudio.nexreceipt.pro.data.local

import androidx.room.TypeConverter
import com.aistudio.nexreceipt.pro.data.model.ReceiptItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReceiptTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromItemList(value: List<ReceiptItem>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toItemList(value: String?): List<ReceiptItem> {
        if (value.isNullOrEmpty()) return emptyList()
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
