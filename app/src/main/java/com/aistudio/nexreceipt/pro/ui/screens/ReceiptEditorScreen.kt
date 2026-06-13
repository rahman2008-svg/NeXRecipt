package com.aistudio.nexreceipt.pro.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.nexreceipt.pro.data.model.Receipt
import com.aistudio.nexreceipt.pro.data.model.ReceiptItem
import com.aistudio.nexreceipt.pro.ui.viewmodel.ReceiptViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptEditorScreen(
    viewModel: ReceiptViewModel,
    onSaveSuccess: (Receipt) -> Unit
) {
    val context = LocalContext.current
    val isEditMode by viewModel.editingReceiptId.collectAsStateWithLifecycle()

    val type by viewModel.editType.collectAsStateWithLifecycle()
    val bName by viewModel.businessName.collectAsStateWithLifecycle()
    val cName by viewModel.customerName.collectAsStateWithLifecycle()
    val rNumber by viewModel.receiptNumber.collectAsStateWithLifecycle()
    val rNotes by viewModel.notes.collectAsStateWithLifecycle()
    val tax by viewModel.taxRate.collectAsStateWithLifecycle()
    val discount by viewModel.discountRate.collectAsStateWithLifecycle()
    val status by viewModel.paymentStatus.collectAsStateWithLifecycle()
    val method by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val currentItems by viewModel.items.collectAsStateWithLifecycle()

    val tempName by viewModel.tempItemName.collectAsStateWithLifecycle()
    val tempQty by viewModel.tempItemQty.collectAsStateWithLifecycle()
    val tempPrice by viewModel.tempItemPrice.collectAsStateWithLifecycle()

    // Subtotals Live Calculations
    val subtotal = currentItems.sumOf { it.total }
    val discAmt = subtotal * (discount / 100.0)
    val taxAmt = (subtotal - discAmt) * (tax / 100.0)
    val finalTotal = subtotal - discAmt + taxAmt

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode != null) "Edit Document" else "Create Document",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    if (isEditMode != null) {
                        IconButton(
                            onClick = { viewModel.clearEditor() },
                            modifier = Modifier.testTag("editor_back_btn")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = Modifier.fillMaxSize().testTag("receipt_editor_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Selector Tab Row
            item {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = type == "RECEIPT",
                        onClick = {
                            viewModel.editType.value = "RECEIPT"
                            viewModel.updateReceiptNumber()
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        modifier = Modifier.testTag("editor_type_receipt_btn")
                    ) {
                        Text("Receipt", fontWeight = FontWeight.Bold)
                    }
                    SegmentedButton(
                        selected = type == "INVOICE",
                        onClick = {
                            viewModel.editType.value = "INVOICE"
                            viewModel.updateReceiptNumber()
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        modifier = Modifier.testTag("editor_type_invoice_btn")
                    ) {
                        Text("Invoice", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. Client & Business Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "BUSINESS & CLIENT DETAIL",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = bName,
                            onValueChange = { viewModel.businessName.value = it },
                            label = { Text("Business Name") },
                            placeholder = { Text("e.g. NexVora Lab's Ofc") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("editor_business_name_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = cName,
                            onValueChange = { viewModel.customerName.value = it },
                            label = { Text("Customer Name") },
                            placeholder = { Text("e.g. Prince AR Abdur Rahman") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("editor_customer_name_input"),
                            singleLine = true
                        )

                        // Document Number & Regenerate Custom block
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = rNumber,
                                onValueChange = { viewModel.receiptNumber.value = it },
                                label = { Text("Document Number") },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                                modifier = Modifier.weight(1f).testTag("editor_doc_number_input"),
                                singleLine = true
                            )

                            IconButton(
                                onClick = { viewModel.updateReceiptNumber() },
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .testTag("editor_regenerate_number_btn")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // 3. Settings Status & Delivery Selectors
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "PAYMENT SETTINGS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // Status Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Payment Status:", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                            SegmentedButtonRowHelper(
                                items = listOf("PAID", "UNPAID", "PENDING"),
                                selectedItem = status,
                                onSelected = { viewModel.paymentStatus.value = it },
                                testTagPrefix = "status"
                            )
                        }

                        // Method Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Payment Method:", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                            SegmentedButtonRowHelper(
                                items = listOf("CASH", "CARD", "BANK"),
                                selectedItem = if(method == "BANK TRANSFER") "BANK" else method,
                                onSelected = {
                                    viewModel.paymentMethod.value = if(it == "BANK") "BANK TRANSFER" else it
                                },
                                testTagPrefix = "method"
                            )
                        }
                    }
                }
            }

            // 4. Products & Items Core Block
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "DOCUMENT ITEMS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { viewModel.tempItemName.value = it },
                            label = { Text("Product / Service Name") },
                            modifier = Modifier.fillMaxWidth().testTag("editor_item_name_input"),
                            singleLine = true
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = tempQty,
                                onValueChange = { viewModel.tempItemQty.value = it },
                                label = { Text("QTY") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("editor_item_qty_input"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = tempPrice,
                                onValueChange = { viewModel.tempItemPrice.value = it },
                                label = { Text("Unit Price ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(2f).testTag("editor_item_price_input"),
                                singleLine = true
                            )
                        }

                        Button(
                            onClick = {
                                if (tempName.isBlank()) {
                                    Toast.makeText(context, "Please enter a product name!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.addTempItem()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("editor_add_item_btn")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Line Item", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(4.dp))

                        if (currentItems.isEmpty()) {
                            Text(
                                "No items added yet. Click 'Add Line Item' above.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            currentItems.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text("${item.quantity} x ${currencyFormatter.format(item.unitPrice)}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(currencyFormatter.format(item.total), fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { viewModel.removeItem(item) },
                                        modifier = Modifier.testTag("editor_remove_item_${index}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC3545))
                                    }
                                }
                                if (index < currentItems.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                }
                            }
                        }
                    }
                }
            }

            // 5. Discount & Tax Sliders
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "ADJUSTMENTS (DISCOUNT & TAX)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // Discount Rates
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Discount:", fontWeight = FontWeight.Medium)
                                Text("${discount.toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Slider(
                                value = discount.toFloat(),
                                onValueChange = { viewModel.discountRate.value = it.toDouble() },
                                valueRange = 0f..50f,
                                modifier = Modifier.testTag("editor_discount_slider")
                            )
                        }

                        // Tax Rates
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tax Rate (VAT/GST):", fontWeight = FontWeight.Medium)
                                Text("${tax.toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            }
                            Slider(
                                value = tax.toFloat(),
                                onValueChange = { viewModel.taxRate.value = it.toDouble() },
                                valueRange = 0f..25f,
                                modifier = Modifier.testTag("editor_tax_slider")
                            )
                        }
                    }
                }
            }

            // 6. Notes & Payment terms
            item {
                OutlinedTextField(
                    value = rNotes,
                    onValueChange = { viewModel.notes.value = it },
                    label = { Text("Notes / Payment Instructions") },
                    placeholder = { Text("e.g. Thank you for doing business with NexVora Lab's Ofc! Payment due within 7 days.") },
                    modifier = Modifier.fillMaxWidth().testTag("editor_notes_input"),
                    minLines = 3,
                    maxLines = 5
                )
            }

            // 7. Live Totals Recap Block
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("TOTAL SUMMARY", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                            Text(currencyFormatter.format(subtotal), style = MaterialTheme.typography.bodyMedium)
                        }

                        if (discAmt > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Discount Amount", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("-${currencyFormatter.format(discAmt)}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFDC3545))
                            }
                        }

                        if (taxAmt > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tax Amount (${tax.toInt()}%)", style = MaterialTheme.typography.bodyMedium)
                                Text("+${currencyFormatter.format(taxAmt)}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("NET DUE TOTAL", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                            Text(currencyFormatter.format(finalTotal), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }

            // 8. Action commit button
            item {
                Button(
                    onClick = {
                        if (currentItems.isEmpty()) {
                            Toast.makeText(context, "Please add at least one line item!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.saveReceipt { savedReceipt ->
                                Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
                                onSaveSuccess(savedReceipt)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("editor_save_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isEditMode != null) "Update Document & Export" else "Save Document & Export",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// Custom segment row helper to show simple Material Choice Chips
@Composable
fun SegmentedButtonRowHelper(
    items: List<String>,
    selectedItem: String,
    onSelected: (String) -> Unit,
    testTagPrefix: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEachIndexed { idx, valStr ->
            FilterChip(
                selected = selectedItem == valStr,
                onClick = { onSelected(valStr) },
                label = { Text(valStr, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                modifier = Modifier.testTag("${testTagPrefix}_chip_${idx}")
            )
        }
    }
}
