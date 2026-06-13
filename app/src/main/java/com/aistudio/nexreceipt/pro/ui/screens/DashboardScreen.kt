package com.aistudio.nexreceipt.pro.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun DashboardScreen(
    viewModel: ReceiptViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onPreviewReceipt: (Receipt) -> Unit
) {
    val totalCount by viewModel.statsTotalCount.collectAsStateWithLifecycle()
    val totalEarnings by viewModel.statsTotalEarnings.collectAsStateWithLifecycle()
    val monthlyEarnings by viewModel.statsMonthlyEarnings.collectAsStateWithLifecycle()
    val receiptCount by viewModel.statsReceiptCount.collectAsStateWithLifecycle()
    val invoiceCount by viewModel.statsInvoiceCount.collectAsStateWithLifecycle()
    val recentItems by viewModel.recentReceipts.collectAsStateWithLifecycle()

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "NEXVORA LAB'S",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                letterSpacing = 1.8.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "NexReceipt Pro",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                    
                    // Profile/Avatar badge
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "AR",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize().testTag("dashboard_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Stats Header Banner Card matching "Earnings Dashboard Card"
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    Color(0xFF0369A1) // Sleek Sky Dark Blue Gradient
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    "Monthly Earnings",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    currencyFormatter.format(monthlyEarnings),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp,
                                        letterSpacing = (-0.5).sp
                                    )
                                )
                            }
                            
                            // Date Tag
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()).uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Blinking Status Indicator Tag
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34D399))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "+12.5% vs last month",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            
                            // Interactive Quick Create Action
                            Button(
                                onClick = {
                                    viewModel.clearEditor()
                                    onNavigateToCreate()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("dashboard_quick_create_btn")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Receipt", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black))
                            }
                        }
                    }
                }
            }

            // Stats Section
            item {
                Text(
                    "FINANCIAL STATISTICS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Value",
                            value = currencyFormatter.format(totalEarnings),
                            icon = Icons.Filled.AccountBalanceWallet,
                            color = Color(0xFFFF9F43), // Sleek Amber
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Active Invoices",
                            value = "$invoiceCount invoices",
                            icon = Icons.Filled.Description,
                            color = MaterialTheme.colorScheme.primary, // Sleek Brand Blue
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Receipts",
                            value = "$receiptCount items",
                            icon = Icons.Filled.ReceiptLong,
                            color = Color(0xFF10B981), // Sleek Emerald Green
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Count",
                            value = "$totalCount total docs",
                            icon = Icons.Filled.BarChart,
                            color = Color(0xFF8B5CF6), // Sleek Purple
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recent Documents Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "RECENT DOCUMENTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    TextButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.testTag("dashboard_view_all_btn")
                    ) {
                        Text(
                            "View All", 
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (recentItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Receipt,
                                    contentDescription = "No receipts",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "No documents yet",
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Your newly created invoices and receipts will appear here.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            } else {
                items(recentItems, key = { it.id }) { receipt ->
                    RecentItemRow(receipt = receipt, onClick = { onPreviewReceipt(receipt) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Icon Rounded Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RecentItemRow(
    receipt: Receipt,
    onClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(receipt.timestamp))

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .testTag("recent_item_${receipt.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (receipt.type == "RECEIPT") "📄" else "💼",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    receipt.customerName,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "${receipt.receiptNumber} • $formattedDate",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Total price & status
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    currencyFormatter.format(receipt.totalAmount),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (receipt.paymentStatus == "PAID") Color(0xFFD1FBEA) else Color(0xFFFEE2E2),
                    contentColor = if (receipt.paymentStatus == "PAID") Color(0xFF028A5B) else Color(0xFFEF4444)
                ) {
                    Text(
                        receipt.paymentStatus,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
