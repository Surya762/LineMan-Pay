package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.data.CollectionRecord
import com.example.ui.AdminSubScreen
import com.example.ui.AppTab
import com.example.ui.HistoryFilter
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allCollections by viewModel.allCollections.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filter by viewModel.historyFilter.collectAsState()
    val selectedReceipt by viewModel.selectedReceipt.collectAsState()

    // Filtered lists
    val filteredList = remember(allCollections, searchQuery, filter) {
        allCollections.filter { record ->
            // Search criteria
            val matchesQuery = searchQuery.isEmpty() ||
                    record.customerName.contains(searchQuery, ignoreCase = true) ||
                    record.serviceNumber.contains(searchQuery, ignoreCase = true)

            // Date filter criteria
            val matchesFilter = when (filter) {
                HistoryFilter.Today -> isTimestampToday(record.timestamp)
                HistoryFilter.ThisWeek -> isTimestampThisWeek(record.timestamp)
                HistoryFilter.ThisMonth -> isTimestampThisMonth(record.timestamp)
            }

            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // AppBar
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

            val avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBlPam2fxHXzRVJ4YVG-Dm17IaMQkrqWwe1NrQVqjFEOq6ixm5oxHITOEMP7AsbwmQx40UWWzHvBfWfYoPubc396PG7J8VA4RmNkhaIyNWPHri1S0IilzW6JMO5o4sgn6BI0xIlO_-Yx5FP5dwQ0vn5JpTiUDkHqnCxAkS72ZNAqMs5uNyiklIrzmYCFcjn8RxK-LQfN14j-FaRLQyn_U8C_XIkvZS1D8FUI66gfQKIbgLNN2vZJDFooPMPEVmnT9otsIVjS_oyJ46R"
            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryBlueContainer, CircleShape)
                    .clickable {
                        viewModel.setAdminSubScreen(AdminSubScreen.Profile)
                        viewModel.selectTab(AppTab.Admin)
                    },
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search name or service number", color = OutlineVariantColor) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = OnSurfaceVariantColor
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = SurfaceLow,
                    focusedContainerColor = SurfaceLow,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = OutlineVariantColor
                ),
                singleLine = true
            )

            // Filter Chips row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChipItem(
                        text = "Today",
                        isSelected = filter == HistoryFilter.Today,
                        onClick = { viewModel.setHistoryFilter(HistoryFilter.Today) }
                    )
                }
                item {
                    FilterChipItem(
                        text = "This Week",
                        isSelected = filter == HistoryFilter.ThisWeek,
                        onClick = { viewModel.setHistoryFilter(HistoryFilter.ThisWeek) }
                    )
                }
                item {
                    FilterChipItem(
                        text = "This Month",
                        isSelected = filter == HistoryFilter.ThisMonth,
                        onClick = { viewModel.setHistoryFilter(HistoryFilter.ThisMonth) }
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceColor)
                            .border(1.dp, OutlineVariantColor, CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filters",
                            tint = OnSurfaceVariantColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // History list
            if (filteredList.isEmpty()) {
                // Empty state view
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(SurfaceLow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No results",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No results found",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceColor
                    )
                    Text(
                        text = "We couldn't find any payments matching your search. Try a different query.",
                        fontSize = 14.sp,
                        color = OnSurfaceVariantColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = {
                        viewModel.updateSearchQuery("")
                        viewModel.setHistoryFilter(HistoryFilter.ThisWeek)
                    }) {
                        Text(
                            text = "Clear all filters",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { item ->
                        HistoryCard(item = item, onClick = {
                            viewModel.selectReceiptForDetail(item)
                        })
                    }
                }
            }
        }
    }

    // Modal Receipt Detail Dialog
    selectedReceipt?.let { receipt ->
        ReceiptDetailDialog(record = receipt, onDismiss = {
            viewModel.selectReceiptForDetail(null)
        })
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryBlueContainer else SurfaceColor)
            .border(1.dp, if (isSelected) Color.Transparent else OutlineVariantColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
            }
            Text(
                text = text,
                color = if (isSelected) Color.White else OnSurfaceColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun HistoryCard(
    item: CollectionRecord,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}")
            .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (!item.serviceNumber.contains("WTR")) Color(0xFFFFF0D4) else SecondaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (!item.serviceNumber.contains("WTR")) Icons.Default.ElectricBolt else Icons.Default.Person,
                    contentDescription = "Person or electric",
                    tint = if (!item.serviceNumber.contains("WTR")) TertiaryColor else PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.customerName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceColor
                    )
                    Text(
                        text = "${item.currency}${String.format("%,.2f", item.billAmount + item.rcAmount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Acc: ${item.serviceNumber}",
                        fontSize = 13.sp,
                        color = OnSurfaceVariantColor
                    )
                    val dateStr = SimpleDateFormat("MMM dd, hh:mm a", Locale.US).format(Date(item.timestamp))
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        color = OnSurfaceVariantColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detail",
                tint = OutlineColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptDetailDialog(
    record: CollectionRecord,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, OutlineVariantColor, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment Receipt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceColor
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Decorative Receipt card inside modal
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    OfficialEasternPowerReceipt(record = record)
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryBlue),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share")
                        }
                    }

                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = OnSurfaceVariantColor)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurfaceColor)
    }
}

// Filter helper functions
private fun isTimestampToday(timestamp: Long): Boolean {
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.US)
    return fmt.format(Date(timestamp)) == fmt.format(Date())
}

private fun isTimestampThisWeek(timestamp: Long): Boolean {
    val currentWeek = SimpleDateFormat("w_yyyy", Locale.US).format(Date())
    val itemWeek = SimpleDateFormat("w_yyyy", Locale.US).format(Date(timestamp))
    return currentWeek == itemWeek
}

private fun isTimestampThisMonth(timestamp: Long): Boolean {
    val currentMonth = SimpleDateFormat("MM_yyyy", Locale.US).format(Date())
    val itemMonth = SimpleDateFormat("MM_yyyy", Locale.US).format(Date(timestamp))
    return currentMonth == itemMonth
}
