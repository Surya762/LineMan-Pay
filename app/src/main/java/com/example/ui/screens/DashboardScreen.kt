package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.CollectionRecord
import com.example.ui.AdminSubScreen
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allCollections by viewModel.allCollections.collectAsState()
    val todaySum by viewModel.todayCollectionsSum.collectAsState()
    val lastCollection by viewModel.lastCollectionRecord.collectAsState()

    // Operator image
    val profileUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDYxljzC1IvdPKH5zHxtmLiSuHpVqH0J7_st5SatGeDUKGmENsVMzHJYqiExAIdnYe28t0Z5e4KiaxmtU1VfG1IWMCKQ7NmuxWvEK1FLowTR2uObnl43YTbt_AGd1_DffoEwPLUwK_7dpc1f4KD1QW-zG9TjzKojSkCiPtZs-jkLHBuIeLtgARVrVV5b061wP9uC9uz1diq_IhQoeAJrc-5KOo1wwYYcaxvm1zX4V71AA7RfHmNGGwCPlDsabw1ptRugPZ8SnlQX_RH"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Custom Header (AppBar style)
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Bolt icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "LineMan Pay",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            // Profile picture
            Image(
                painter = rememberAsyncImagePainter(profileUrl),
                contentDescription = "User avatar",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Good morning,",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceColor
                    )
                    Text(
                        text = "Arjun Sharma",
                        fontSize = 20.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Target Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TODAY'S TARGET",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariantColor,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${String.format("%,.0f", todaySum)} / ₹25,000",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceColor
                                )
                            }

                            val percentage = ((todaySum / 25000.0) * 100).coerceIn(0.0, 100.0)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.testTag("trend_row")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Trending icon",
                                    tint = PrimaryBlue
                                )
                                Text(
                                    text = "${percentage.toInt()}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }
            }

            // Bento Grid Actions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // New Payment (Takes 2 Columns equivalent)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(112.dp)
                            .testTag("bento_new_payment")
                            .border(1.dp, Color.Transparent, RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectTab(AppTab.NewPay) },
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
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "New payment icon",
                                    tint = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = "Action",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "New Payment",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Bottom Row: History, Profile, Admin Dashboard
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // History Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp)
                                .testTag("bento_history")
                                .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp))
                                .clickable { viewModel.selectTab(AppTab.History) },
                            colors = CardDefaults.cardColors(containerColor = SurfaceHighest),
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
                                        .background(Color(0xFFFFA000).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "History icon",
                                        tint = Color(0xFFFFA000)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Records",
                                        fontSize = 12.sp,
                                        color = OnSurfaceVariantColor
                                    )
                                    Text(
                                        text = "History",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceColor
                                    )
                                }
                            }
                        }

                        // Profile / Settings Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp)
                                .testTag("bento_profile")
                                .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.setAdminSubScreen(AdminSubScreen.Profile)
                                    viewModel.selectTab(AppTab.Admin)
                                },
                            colors = CardDefaults.cardColors(containerColor = SurfaceHigh),
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
                                        .background(SecondaryColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile icon",
                                        tint = SecondaryColor
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Account",
                                        fontSize = 12.sp,
                                        color = OnSurfaceVariantColor
                                    )
                                    Text(
                                        text = "Profile",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceColor
                                    )
                                }
                            }
                        }

                        // Admin / Management Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp)
                                .testTag("bento_admin")
                                .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.setAdminSubScreen(AdminSubScreen.Dashboard)
                                    viewModel.selectTab(AppTab.Admin)
                                },
                            colors = CardDefaults.cardColors(containerColor = SecondaryContainer),
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
                                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Dashboard,
                                        contentDescription = "Admin icon",
                                        tint = PrimaryBlue
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Management",
                                        fontSize = 12.sp,
                                        color = OnSecondaryContainer
                                    )
                                    Text(
                                        text = "Admin",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Collections List
            item {
                Text(
                    text = "Recent Collections",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceColor,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            val recentList = allCollections.take(3)
            if (recentList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No collections recorded yet.",
                                color = OnSurfaceVariantColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(recentList) { item ->
                    RecentCollectionItem(item = item)
                }
            }
        }
    }
}

@Composable
fun RecentCollectionItem(item: CollectionRecord) {
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (!item.serviceNumber.contains("WTR")) Color(0xFFFFF0D4) else SecondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (!item.serviceNumber.contains("WTR")) Icons.Default.ElectricBolt else Icons.Default.WaterDrop,
                        contentDescription = "Collection icon",
                        tint = if (!item.serviceNumber.contains("WTR")) TertiaryColor else PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.customerName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceColor
                    )
                    Text(
                        text = "Inv #${item.id + 45910} • ${formatTime(item.timestamp)}",
                        fontSize = 12.sp,
                        color = OnSurfaceVariantColor
                    )
                }
            }

            Text(
                text = "${item.currency}${String.format("%,.2f", item.billAmount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
    }
}

fun formatTime(timestamp: Long): String {
    val fmt = SimpleDateFormat("hh:mm a", Locale.US)
    return fmt.format(Date(timestamp))
}
