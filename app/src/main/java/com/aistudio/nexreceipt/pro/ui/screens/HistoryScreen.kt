package com.aistudio.nexreceipt.pro.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.nexreceipt.pro.data.model.Receipt
import com.aistudio.nexreceipt.pro.ui.viewmodel.ReceiptViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ReceiptViewModel,
    onNavigateToEdit: (Receipt) -> Unit,
    onPreviewReceipt: (Receipt) -> Unit
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val receiptsList by viewModel.filteredReceipts.collectAsStateWithLifecycle()

    var showDeleteDialogFor by remember { mutableStateOf<Receipt?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Document History",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = Modifier.fillMaxSize().testTag("history_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // 1. Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by name, business, or ID...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("history_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // 2. Filter tabs
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                SegmentedButton(
                    selected = filterType == "ALL",
                    onClick = { viewModel.setFilterType("ALL") },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                    modifier = Modifier.testTag("filter_all_btn")
                ) {
                    Text("All")
                }
                SegmentedButton(
                    selected = filterType == "RECEIPT",
                    onClick = { viewModel.setFilterType("RECEIPT") },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                    modifier = Modifier.testTag("filter_receipts_btn")
                ) {
                    Text("Receipts")
                }
                SegmentedButton(
                    selected = filterType == "INVOICE",
                    onClick = { viewModel.setFilterType("INVOICE") },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                    modifier = Modifier.testTag("filter_invoices_btn")
                ) {
                    Text("Invoices")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Main Documents List
            if (receiptsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No matching records",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Try searching with another keyword or start creating a new receipt/invoice.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(receiptsList, key = { it.id }) { receipt ->
                        HistoryCard(
                            receipt = receipt,
                            onClick = { onPreviewReceipt(receipt) },
                            onEdit = { onNavigateToEdit(receipt) },
                            onDelete = { showDeleteDialogFor = receipt }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Delete confirmation pop-up dialog
    if (showDeleteDialogFor != null) {
        val targetReceipt = showDeleteDialogFor!!
        AlertDialog(
            onDismissRequest = { showDeleteDialogFor = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReceipt(targetReceipt)
                        showDeleteDialogFor = null
                        Toast.makeText(context, "Deleted record!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("dialog_confirm_btn")
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold, color = Color(0xFFDC3545))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialogFor = null },
                    modifier = Modifier.testTag("dialog_dismiss_btn")
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            },
            title = { Text("Delete Document?") },
            text = { Text("Are you sure you want to permanently delete receipt ${targetReceipt.receiptNumber}?") },
            icon = { Icon(Icons.Default.Delete, contentDescription = "Trash Icon", tint = Color(0xFFDC3545)) }
        )
    }
}

@Composable
fun HistoryCard(
    receipt: Receipt,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val formattedDate = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(receipt.timestamp))

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_card_${receipt.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Number + Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (receipt.type == "RECEIPT") Icons.Default.Receipt else Icons.Default.Article,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        receipt.receiptNumber,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (receipt.type == "RECEIPT") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (receipt.type == "RECEIPT") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text(
                        receipt.type,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "BILL TO / CLIENT", 
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ), 
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(receipt.customerName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "TOTAL AMOUNT", 
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ), 
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(currencyFormatter.format(receipt.totalAmount), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(6.dp))

            // Bottom control actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    formattedDate,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Update Item
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("history_card_edit_btn_${receipt.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.secondary)
                    }

                    // Trash Item
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("history_card_delete_btn_${receipt.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC3545))
                    }
                }
            }
        }
    }
}
