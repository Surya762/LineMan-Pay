package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.OtpState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val mobileNumber by viewModel.mobileNumber.collectAsState()
    val otpState by viewModel.otpState.collectAsState()
    val enteredOtp by viewModel.enteredOtp.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFDAE2FF),
                        BackgroundColor
                    ),
                    radius = 2000f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card")
                    .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo Header
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlueContainer)
                            .testTag("logo_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Bolt Logo",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "LineMan Pay",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Bill Collection Portal",
                        fontSize = 14.sp,
                        color = OnSurfaceVariantColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedContent(
                        targetState = otpState,
                        transitionSpec = {
                            fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                        },
                        label = "login_form_transition"
                    ) { state ->
                        when (state) {
                            is OtpState.OtpSent -> {
                                // OTP Verification Form
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Enter 6-digit OTP",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnSurfaceColor,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    OutlinedTextField(
                                        value = enteredOtp,
                                        onValueChange = { viewModel.updateEnteredOtp(it) },
                                        placeholder = { Text("xxxxxx", color = OutlineVariantColor) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Smartphone,
                                                contentDescription = "Phone Icon",
                                                tint = PrimaryBlue
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("otp_input"),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryBlue,
                                            unfocusedBorderColor = OutlineVariantColor
                                        ),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Simulated code sent: ${state.otp}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { viewModel.verifyOtp() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .testTag("verify_otp_button"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                    ) {
                                        Text("Verify OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            is OtpState.Verifying -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryBlue)
                                }
                            }
                            is OtpState.Sending -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryBlue)
                                }
                            }
                            else -> {
                                // Default Phone Number Form
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Mobile Number",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnSurfaceColor,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    OutlinedTextField(
                                        value = mobileNumber,
                                        onValueChange = { viewModel.updateMobileNumber(it) },
                                        placeholder = { Text("Enter 10-digit number", color = OutlineVariantColor) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Smartphone,
                                                contentDescription = "Phone Icon",
                                                tint = PrimaryBlue
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("mobile_input"),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryBlue,
                                            unfocusedBorderColor = OutlineVariantColor
                                        ),
                                        singleLine = true
                                    )

                                    if (state is OtpState.Error) {
                                        Text(
                                            text = state.message,
                                            color = OverdueRed,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        LaunchedEffect(state) {
                                            kotlinx.coroutines.delay(4000)
                                            viewModel.clearAuthError()
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.requestOtp()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .testTag("get_otp_button"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text("Get OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = "Arrow Icon",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { /* simulated action */ },
                        modifier = Modifier.testTag("trouble_button")
                    ) {
                        Text(
                            text = "Trouble logging in?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Informational Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("info_card")
                    .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SecondaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info Icon",
                        tint = OnSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "This portal is for authorized field agents only. Please ensure you are in a location with stable network connectivity before requesting OTP.",
                        fontSize = 14.sp,
                        color = OnSecondaryContainer,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.opacity(0.7f)
            ) {
                Text(
                    text = "Powered by ",
                    fontSize = 12.sp,
                    color = OnSurfaceVariantColor
                )
                // Simulated Firebase logo icon
                Text(
                    text = "🔥 Firebase",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA000)
                )
            }

            Text(
                text = "© 2024 LineMan Pay Utility Services",
                fontSize = 12.sp,
                color = OutlineColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// Simple modifier extension for alpha/opacity
fun Modifier.opacity(alpha: Float): Modifier = this.then(
    Modifier.background(Color.Unspecified.copy(alpha = alpha))
)
