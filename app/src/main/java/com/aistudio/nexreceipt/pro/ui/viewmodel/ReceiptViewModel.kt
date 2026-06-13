package com.aistudio.nexreceipt.pro.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.nexreceipt.pro.data.local.ReceiptDatabase
import com.aistudio.nexreceipt.pro.data.model.Receipt
import com.aistudio.nexreceipt.pro.data.model.ReceiptItem
import com.aistudio.nexreceipt.pro.data.repository.ReceiptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ReceiptViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReceiptRepository
    val allReceipts: StateFlow<List<Receipt>>

    init {
        val database = ReceiptDatabase.getDatabase(application)
        repository = ReceiptRepository(database.receiptDao())
        allReceipts = repository.allReceipts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow("ALL") // "ALL", "RECEIPT", "INVOICE"
    val filterType = _filterType.asStateFlow()

    val filteredReceipts: StateFlow<List<Receipt>> = combine(
        allReceipts, _searchQuery, _filterType
    ) { list, query, type ->
        list.filter { receipt ->
            val matchesType = type == "ALL" || receipt.type.equals(type, ignoreCase = true)
            val matchesQuery = receipt.businessName.contains(query, ignoreCase = true) ||
                    receipt.customerName.contains(query, ignoreCase = true) ||
                    receipt.receiptNumber.contains(query, ignoreCase = true)
            matchesType && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Editing State
    var editingReceiptId = MutableStateFlow<Long?>(null)
    val editType = MutableStateFlow("RECEIPT") // "RECEIPT" or "INVOICE"
    val businessName = MutableStateFlow("")
    val customerName = MutableStateFlow("")
    val receiptNumber = MutableStateFlow("")
    val notes = MutableStateFlow("")
    val taxRate = MutableStateFlow(0.0)
    val discountRate = MutableStateFlow(0.0)
    val paymentStatus = MutableStateFlow("PAID") // "PAID", "UNPAID", "PENDING"
    val paymentMethod = MutableStateFlow("CASH") // "CASH", "CARD", "BANK TRANSFER"
    val dueDate = MutableStateFlow<Long?>(null)

    private val _items = MutableStateFlow<List<ReceiptItem>>(emptyList())
    val items = _items.asStateFlow()

    // Temp lists for easy textfields input
    val tempItemName = MutableStateFlow("")
    val tempItemQty = MutableStateFlow("")
    val tempItemPrice = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun addTempItem() {
        val name = tempItemName.value.trim()
        val qty = tempItemQty.value.toIntOrNull() ?: 1
        val price = tempItemPrice.value.toDoubleOrNull() ?: 0.0

        if (name.isNotEmpty() && price >= 0) {
            val newList = _items.value + ReceiptItem(name, qty, price)
            _items.value = newList
            // Reset fields
            tempItemName.value = ""
            tempItemQty.value = ""
            tempItemPrice.value = ""
        }
    }

    fun removeItem(item: ReceiptItem) {
        _items.value = _items.value.filter { it != item }
    }

    fun clearEditor() {
        editingReceiptId.value = null
        editType.value = "RECEIPT"
        businessName.value = ""
        customerName.value = ""
        receiptNumber.value = generateReceiptNumber("RECEIPT")
        notes.value = ""
        taxRate.value = 0.0
        discountRate.value = 0.0
        paymentStatus.value = "PAID"
        paymentMethod.value = "CASH"
        dueDate.value = null
        _items.value = emptyList()
        tempItemName.value = ""
        tempItemQty.value = ""
        tempItemPrice.value = ""
    }

    fun loadReceipt(receipt: Receipt) {
        editingReceiptId.value = receipt.id
        editType.value = receipt.type
        businessName.value = receipt.businessName
        customerName.value = receipt.customerName
        receiptNumber.value = receipt.receiptNumber
        notes.value = receipt.notes
        taxRate.value = receipt.taxRate
        discountRate.value = receipt.discountRate
        paymentStatus.value = receipt.paymentStatus
        paymentMethod.value = receipt.paymentMethod
        dueDate.value = receipt.dueDate
        _items.value = receipt.items
        tempItemName.value = ""
        tempItemQty.value = ""
        tempItemPrice.value = ""
    }

    fun generateReceiptNumber(type: String): String {
        val prefix = if (type == "RECEIPT") "REC" else "INV"
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val dateStr = formatter.format(Date())
        val randomNum = Random.nextInt(1000, 9999)
        return "$prefix-$dateStr-$randomNum"
    }

    fun updateReceiptNumber() {
        receiptNumber.value = generateReceiptNumber(editType.value)
    }

    fun saveReceipt(onSuccess: (Receipt) -> Unit) {
        viewModelScope.launch {
            val itemsList = _items.value
            val sub = itemsList.sumOf { it.total }
            val discAmt = sub * (discountRate.value / 100.0)
            val taxAmt = (sub - discAmt) * (taxRate.value / 100.0)
            val finalTotal = sub - discAmt + taxAmt

            val receipt = Receipt(
                id = editingReceiptId.value ?: 0,
                type = editType.value,
                businessName = businessName.value.ifBlank { "NexVora Lab's Ofc" },
                customerName = customerName.value.ifBlank { "Valued Customer" },
                receiptNumber = receiptNumber.value.ifBlank { generateReceiptNumber(editType.value) },
                timestamp = System.currentTimeMillis(),
                items = itemsList,
                taxRate = taxRate.value,
                discountRate = discountRate.value,
                taxAmount = taxAmt,
                discountAmount = discAmt,
                subtotal = sub,
                totalAmount = finalTotal,
                notes = notes.value,
                paymentStatus = paymentStatus.value,
                paymentMethod = paymentMethod.value,
                dueDate = if (editType.value == "INVOICE") dueDate.value else null
            )

            if (receipt.id == 0L) {
                val newId = repository.insertReceipt(receipt)
                onSuccess(receipt.copy(id = newId))
            } else {
                repository.updateReceipt(receipt)
                onSuccess(receipt)
            }
        }
    }

    fun deleteReceipt(receipt: Receipt) {
        viewModelScope.launch {
            repository.deleteReceipt(receipt)
        }
    }

    // Dashboard Statistics Calculation Details
    val statsTotalCount = allReceipts.map { list -> list.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val statsInvoiceCount = allReceipts.map { list -> list.count { it.type == "INVOICE" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val statsReceiptCount = allReceipts.map { list -> list.count { it.type == "RECEIPT" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val statsTotalEarnings = allReceipts.map { list -> list.sumOf { it.totalAmount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val statsMonthlyEarnings = allReceipts.map { list ->
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val cal = Calendar.getInstance()
        list.filter {
            cal.timeInMillis = it.timestamp
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }.sumOf { it.totalAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val recentReceipts = allReceipts.map { list ->
        list.take(3)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
