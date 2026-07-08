package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Arjun Sharma headshot from html
    val profileUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCmJ2bLfWgovEvdGyzlckYAYBt2S25atYHKP_cPqXMbWUSLVDA5-KLukU_sf8T6uxRg_khch-PtBYV68wwgePsJxVCVg3sfdqTJjmetUbMIJgX77ybg_ILcoR4etSrY_MEi6BhBfBzE795flX8l7Xe1RUWSpSDe1vh7VU5VbqtalrwTYBahbt6kvBfjXG4yqkcADgDooqrlT-HMZAbvsrtfMq2HrZwx_Vs1fmonJi8VsE5z5G-qPug_rj-oJKIVl3fUonkbvLi53psb"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
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
                    contentDescription = "Bolt icon",
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

            // Small profile pic
            Image(
                painter = rememberAsyncImagePainter(profileUrl),
                contentDescription = "Mini Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, OutlineVariantColor, CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Operator Identity Card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(profileUrl),
                    contentDescription = "Main Profile picture",
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .testTag("profile_avatar_image"),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "Arjun Sharma",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceColor
                )

                Text(
                    text = "+91 98765 43210",
                    fontSize = 16.sp,
                    color = OnSurfaceVariantColor
                )

                // Operator Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SecondaryContainer)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Field Operator ID: #OM-8821",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSecondaryContainer
                    )
                }
            }

            // Settings List Block
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Account Settings",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OutlineColor,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )

                // Printer Settings Item
                SettingsRow(
                    icon = Icons.Default.Print,
                    title = "Printer Settings",
                    subtext = "Bluetooth: Connected (Zebra-420)",
                    subtextColor = PrimaryBlue,
                    onClick = {
                        Toast.makeText(context, "Printer connected successfully!", Toast.LENGTH_SHORT).show()
                    }
                )

                // Change Password Item
                SettingsRow(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtext = "Last changed 24 days ago",
                    onClick = {
                        Toast.makeText(context, "Loading password dialog...", Toast.LENGTH_SHORT).show()
                    }
                )

                // App Version Item
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
                                    .background(SurfaceColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Version",
                                    tint = PrimaryBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "App Version",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceColor
                                )
                                Text(
                                    text = "v2.4.1 (Stable Build)",
                                    fontSize = 14.sp,
                                    color = OnSurfaceVariantColor
                                )
                            }
                        }

                        // Up to Date pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFDBCF))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Up to date",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7B2600)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Logout Button
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("logout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OverdueRed)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LineMan Pay Ecosystem • Powered by EnergyCorp",
                    fontSize = 12.sp,
                    color = OutlineColor,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "SECURE FIELD TERMINAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = OutlineColor.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtext: String,
    subtextColor: Color = OnSurfaceVariantColor,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
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
                        .background(SurfaceColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = PrimaryBlue
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceColor
                    )
                    Text(
                        text = subtext,
                        fontSize = 14.sp,
                        color = subtextColor
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = OutlineVariantColor
            )
        }
    }
}
