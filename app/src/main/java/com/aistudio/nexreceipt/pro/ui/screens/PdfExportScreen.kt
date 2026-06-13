package com.aistudio.nexreceipt.pro.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.aistudio.nexreceipt.pro.data.model.Receipt
import com.aistudio.nexreceipt.pro.utils.PdfGenerator
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfExportScreen(
    receipt: Receipt,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val formattedDate = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(receipt.timestamp))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Export Document",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("pdf_back_btn")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = Modifier.fillMaxSize().testTag("pdf_export_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual Preview Voucher Card representation
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Voucher Top header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(receipt.type.uppercase(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            Text(receipt.businessName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (receipt.paymentStatus == "PAID") Color(0xFFE2FBEA) else Color(0xFFFFECEE),
                            contentColor = if (receipt.paymentStatus == "PAID") Color(0xFF028A5B) else Color(0xFFDC3545)
                        ) {
                            Text(
                                receipt.paymentStatus,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Meta Row details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("DOCUMENT ID", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Text(receipt.receiptNumber, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("DATE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Text(formattedDate, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("BILL TO / CUSTOMER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Text(receipt.customerName, fontWeight = FontWeight.Bold)
                    Text("Payment channel: ${receipt.paymentMethod}", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("LINE ITEMS (${receipt.items.size})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(6.dp))

                    receipt.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}x ${item.name}", modifier = Modifier.weight(1f), maxLines = 1)
                            Text(currencyFormatter.format(item.total), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Cost totals details
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(currencyFormatter.format(receipt.subtotal))
                        }

                        if (receipt.discountAmount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Discount (${receipt.discountRate.toInt()}%):", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("-${currencyFormatter.format(receipt.discountAmount)}", color = Color(0xFFDC3545))
                            }
                        }

                        if (receipt.taxAmount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tax Model (${receipt.taxRate.toInt()}%):", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+${currencyFormatter.format(receipt.taxAmount)}")
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GRAND TOTAL:", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                            Text(currencyFormatter.format(receipt.totalAmount), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    if (receipt.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Notes:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                Text(receipt.notes, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Text(
                "EXPORT CONTROL ACTIONS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.1.sp
            )

            // PDF Action row controls
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    // Actions 1: SHARE FILE
                    ListItem(
                        headlineContent = { Text("Share PDF Document", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Send via email, WhatsApp, or slack") },
                        leadingContent = { Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier
                            .testTag("pdf_share_btn")
                            .clickable { sharePdf(context, receipt) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Actions 2: SAVE LOCALLY
                    ListItem(
                        headlineContent = { Text("Save to Downloads Folder", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Stores PDF securely in local system storage") },
                        leadingContent = { Icon(Icons.Default.Download, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                        modifier = Modifier
                            .testTag("pdf_save_downloads_btn")
                            .clickable {
                                val savedUri = PdfGenerator.savePdfToDownloads(context, receipt)
                                if (savedUri != null) {
                                    Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save PDF locally.", Toast.LENGTH_SHORT).show()
                                }
                            },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Share PDF function helper using native intent resolver and FileProvider
private fun sharePdf(context: Context, receipt: Receipt) {
    val file = PdfGenerator.generatePdf(context, receipt)
    if (file != null && file.exists()) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "com.aistudio.nexreceipt.pro.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "${receipt.type} ${receipt.receiptNumber}")
                putExtra(Intent.EXTRA_TEXT, "Here is your ${receipt.type.lowercase()} from ${receipt.businessName}.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Document via:"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Failed to create PDF file.", Toast.LENGTH_SHORT).show()
    }
}
