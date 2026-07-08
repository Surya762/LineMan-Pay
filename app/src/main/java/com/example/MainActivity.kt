package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OutlineVariantColor
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SurfaceColor
import com.example.ui.theme.SurfaceLow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()

                AnimatedContent(
                    targetState = isLoggedIn,
                    transitionSpec = {
                        slideInVertically { it } + fadeIn() togetherWith
                                slideOutVertically { -it } + fadeOut()
                    },
                    label = "main_auth_transition"
                ) { loggedIn ->
                    if (!isLoggedIn) {
                        LoginScreen(viewModel = viewModel)
                    } else {
                        MainAppContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val adminSubScreen by viewModel.adminSubScreen.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomNavigationBar(
                activeTab = activeTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    AppTab.Home -> {
                        DashboardScreen(viewModel = viewModel)
                    }
                    AppTab.NewPay -> {
                        NewPaymentScreen(viewModel = viewModel)
                    }
                    AppTab.History -> {
                        HistoryScreen(viewModel = viewModel)
                    }
                    AppTab.Admin -> {
                        // For the Admin tab, we show either Profile or Administrative Overview
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Sleek Administrative Segment/Tabs at the top to toggle between Profile & Administrative Overview
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceColor)
                                    .border(1.dp, OutlineVariantColor)
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.setAdminSubScreen(AdminSubScreen.Profile) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (adminSubScreen == AdminSubScreen.Profile) PrimaryBlue else SurfaceLow
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        "Operator Profile",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (adminSubScreen == AdminSubScreen.Profile) Color.White else PrimaryBlue
                                    )
                                }

                                Button(
                                    onClick = { viewModel.setAdminSubScreen(AdminSubScreen.Dashboard) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (adminSubScreen == AdminSubScreen.Dashboard) PrimaryBlue else SurfaceLow
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        "Admin Overview",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (adminSubScreen == AdminSubScreen.Dashboard) Color.White else PrimaryBlue
                                    )
                                }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                if (adminSubScreen == AdminSubScreen.Profile) {
                                    ProfileScreen(viewModel = viewModel)
                                } else {
                                    AdminOverviewScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(
    activeTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bottom_nav_bar")
            .navigationBarsPadding(),
        containerColor = SurfaceColor,
        tonalElevation = 8.dp
    ) {
        // Home Tab
        NavigationBarItem(
            selected = activeTab == AppTab.Home,
            onClick = { onTabSelected(AppTab.Home) },
            icon = {
                Icon(
                    imageVector = if (activeTab == AppTab.Home) Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                indicatorColor = PrimaryBlueContainer,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        // New Pay Tab
        NavigationBarItem(
            selected = activeTab == AppTab.NewPay,
            onClick = { onTabSelected(AppTab.NewPay) },
            icon = {
                Icon(
                    imageVector = if (activeTab == AppTab.NewPay) Icons.Default.AddCircle else Icons.Outlined.AddCircleOutline,
                    contentDescription = "New Pay"
                )
            },
            label = { Text("New Pay", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                indicatorColor = PrimaryBlueContainer,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("nav_tab_new_pay")
        )

        // History Tab
        NavigationBarItem(
            selected = activeTab == AppTab.History,
            onClick = { onTabSelected(AppTab.History) },
            icon = {
                Icon(
                    imageVector = if (activeTab == AppTab.History) Icons.Default.History else Icons.Outlined.History,
                    contentDescription = "History"
                )
            },
            label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                indicatorColor = PrimaryBlueContainer,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("nav_tab_history")
        )

        // Admin Tab
        NavigationBarItem(
            selected = activeTab == AppTab.Admin,
            onClick = { onTabSelected(AppTab.Admin) },
            icon = {
                Icon(
                    imageVector = if (activeTab == AppTab.Admin) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = "Admin"
                )
            },
            label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryBlue,
                indicatorColor = PrimaryBlueContainer,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("nav_tab_admin")
        )
    }
}
