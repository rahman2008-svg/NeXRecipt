package com.aistudio.nexreceipt.pro.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.nexreceipt.pro.ui.viewmodel.ReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ReceiptViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings & Info",
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
        modifier = Modifier.fillMaxSize().testTag("settings_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile/About Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Initials avatar badge
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.BusinessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "NexReceipt Pro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(
                            "Production Engine v1.0.0",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoDetailRow(
                            label = "Developer",
                            valStr = "Prince AR Abdur Rahman",
                            modifier = Modifier.weight(1f)
                        )
                        InfoDetailRow(
                            label = "Company",
                            valStr = "NexVora Lab's Ofc",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Theme custom block
            Text(
                "PREFERENCES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.1.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Dark Theme Mode", fontWeight = FontWeight.Bold)
                            Text("Enables comfortable night reading", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onThemeToggle,
                        modifier = Modifier.testTag("settings_theme_switch")
                    )
                }
            }

            // Administrative actions block
            Text(
                "DATA MANAGEMENT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.1.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column {
                    // Contact item link
                    ListItem(
                        headlineContent = { Text("Contact Support", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Get in touch with NexVora Lab's Ofc") },
                        leadingContent = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier
                            .testTag("settings_contact_btn")
                            .clickable {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:princearabdurrahman57@gmail.com")
                                }
                                try {
                                    context.startActivity(emailIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No email client app found.", Toast.LENGTH_SHORT).show()
                                }
                            },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Delete item link
                    ListItem(
                        headlineContent = { Text("Clear All Database Data", fontWeight = FontWeight.Bold, color = Color(0xFFDC3545)) },
                        supportingContent = { Text("Permanently wipes out all receipts and statistics") },
                        leadingContent = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC3545)) },
                        modifier = Modifier
                            .testTag("settings_clear_data_btn")
                            .clickable {
                                showDeleteAllDialog = true
                            },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer legal note block
            Text(
                "Copyright © 2026 NexVora Lab's Ofc.\nAll rights reserved. Highly Optimized Native Engine.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Delete All confirmations block
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Under viewmodel, clear items by deleting each
                        viewModel.allReceipts.value.forEach {
                            viewModel.deleteReceipt(it)
                        }
                        showDeleteAllDialog = false
                        Toast.makeText(context, "Full dataset wiped!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.testTag("settings_purge_confirm_btn")
                ) {
                    Text("Purge Everything", fontWeight = FontWeight.Bold, color = Color(0xFFDC3545))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            },
            title = { Text("Purge Database?") },
            text = { Text("Warning! This action is irreversible. All generated financial receipts, invoices, client records, and statistics of NexReceipt Pro will be cleared.") },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC3545)) }
        )
    }
}

@Composable
fun InfoDetailRow(
    label: String,
    valStr: String,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            valStr,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
