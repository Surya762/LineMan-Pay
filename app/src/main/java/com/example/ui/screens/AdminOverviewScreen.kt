package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.CollectionRecord
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOverviewScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalCount by viewModel.totalPaymentCount.collectAsState()
    val todayRupeesSum by viewModel.todayCollectionsSumInRupees.collectAsState()
    val monthRupeesSum by viewModel.monthCollectionsSumInRupees.collectAsState()
    val allCollections by viewModel.allCollections.collectAsState()

    // Filter only Rupee collections for the admin dashboard recent transactions to match the mockup exactly
    val rupeeCollections: List<CollectionRecord> = remember(allCollections) {
        allCollections.filter { it.currency == "₹" }
    }

    // Admin profile photo from html
    val profileUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD2Ms6w00ih_PAR2Y9aU8t4-vX0ib2XwhIFs9anrRrJ4JEVOzKHd0LFGiknLX_0lyES5m37iiv-sKkbQKA0g_LQPfHP9vk4J6p-aGPNBDDylErp5Aa9iugVE6LYJj64Xyf6Y70lPCoGmQs5WUG03olZiX90sstWwuiVIiNIWum8UYAuEl889F5qvQeNFpRQ7dLHcf6XiSxQWFKgUY7jtMPka_q89oXIauDYShrJphKm_Fese78_QZjT5N31s6Eps__V8Y58U4tEUhIw"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(SurfaceColor)
                .border(1.dp, OutlineVariantColor)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Bolt Logo",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LineMan Pay",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { Toast.makeText(context, "Search click", Toast.LENGTH_SHORT).show() }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = OnSurfaceVariantColor)
                }

                Image(
                    painter = rememberAsyncImagePainter(profileUrl),
                    contentDescription = "Admin Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, OutlineVariantColor, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title & Action Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text(
                            text = "Administrative Overview",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceColor
                        )
                        Text(
                            text = "Real-time collection metrics and agent activity.",
                            fontSize = 14.sp,
                            color = OnSurfaceVariantColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Export CSV
                        OutlinedButton(
                            onClick = { Toast.makeText(context, "Exporting CSV...", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, OutlineColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceColor)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.FileDownload, contentDescription = "CSV", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export CSV", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Search All Payments
                        Button(
                            onClick = { Toast.makeText(context, "Searching all records...", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier
                                .weight(1.1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Search All", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Stats Bento Row 1: Today & Month Collections
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Today Collections Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLow),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SecondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Today, contentDescription = "Today", tint = PrimaryBlue)
                                }
                                Text(
                                    text = "+12% vs yesterday",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                            Column {
                                Text(text = "TODAY COLLECTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariantColor)
                                Text(
                                    text = "₹${String.format("%,.0f", todayRupeesSum)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceColor
                                )
                            }
                        }
                    }

                    // Month Collections Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLow),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SuccessContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Month", tint = SuccessGreen)
                                }

                                // Custom micro Canvas bar chart as specified by guidelines
                                Canvas(modifier = Modifier.size(50.dp, 28.dp)) {
                                    val barWidth = 6.dp.toPx()
                                    val barSpacing = 4.dp.toPx()
                                    val heights = listOf(0.3f, 0.55f, 0.4f, 0.85f)
                                    heights.forEachIndexed { i, h ->
                                        drawRoundRect(
                                            color = PrimaryBlue,
                                            topLeft = androidx.compose.ui.geometry.Offset(
                                                x = i * (barWidth + barSpacing),
                                                y = size.height - (h * size.height)
                                            ),
                                            size = androidx.compose.ui.geometry.Size(barWidth, h * size.height),
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                                        )
                                    }
                                }
                            }
                            Column {
                                Text(text = "MONTH COLLECTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariantColor)
                                Text(
                                    text = "₹${String.format("%,.0f", monthRupeesSum)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceColor
                                )
                            }
                        }
                    }
                }
            }

            // Stats Bento Row 2: Payment Count & System Health
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Payment Count Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLow),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Receipts", tint = OverdueRed)
                                }
                                Text(
                                    text = "890 target",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OverdueRed
                                )
                            }
                            Column {
                                Text(text = "PAYMENT COUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariantColor)
                                Text(
                                    text = "$totalCount",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceColor
                                )
                            }
                        }
                    }

                    // System Health Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .border(1.dp, Color.Transparent, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CloudQueue, contentDescription = "Cloud status", tint = Color.White)
                            }
                            Column {
                                Text(text = "SYSTEM HEALTH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                                Text(
                                    text = "Optimal",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SuccessGreen)
                                    )
                                    Text(
                                        text = "Server Online",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Transactions Table Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceColor
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter table", tint = OnSurfaceVariantColor)
                        }
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = OnSurfaceVariantColor)
                        }
                    }
                }
            }

            // Detailed Transactions List
            if (rupeeCollections.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No administrative transactions found.", color = OnSurfaceVariantColor)
                        }
                    }
                }
            } else {
                items(rupeeCollections) { item ->
                    TransactionTableRow(record = item)
                }
            }

            // Pagination Indicator
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Showing ${rupeeCollections.size} of 2,840 records",
                            fontSize = 13.sp,
                            color = OnSurfaceVariantColor,
                            fontWeight = FontWeight.Medium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(36.dp)
                                    .border(1.dp, OutlineVariantColor, RoundedCornerShape(8.dp))
                            ) {
                                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous page", modifier = Modifier.size(20.dp))
                            }
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(36.dp)
                                    .border(1.dp, OutlineVariantColor, RoundedCornerShape(8.dp))
                            ) {
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next page", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionTableRow(record: CollectionRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Short name bubble
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (record.status) {
                                "Paid" -> SuccessContainer
                                "Pending" -> Color(0xFFFFF0D4)
                                else -> OverdueContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initialText = record.customerName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase()
                    Text(
                        text = initialText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (record.status) {
                            "Paid" -> SuccessGreen
                            "Pending" -> Color(0xFFFFA000)
                            else -> OverdueRed
                        }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = record.customerName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceColor
                    )
                    Text(
                        text = "Acc: ${record.serviceNumber} • ${record.agentName}",
                        fontSize = 12.sp,
                        color = OnSurfaceVariantColor
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${record.currency}${String.format("%,.2f", record.billAmount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceColor
                )

                // Colored Pill badge matching mockups
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (record.status) {
                                "Paid" -> SuccessContainer
                                "Pending" -> Color(0xFFFFEEDB)
                                else -> OverdueContainer
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = record.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (record.status) {
                            "Paid" -> SuccessGreen
                            "Pending" -> Color(0xFFE65100)
                            else -> OverdueRed
                        }
                    )
                }
            }
        }
    }
}
