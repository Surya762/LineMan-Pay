package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.CollectionRecord
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPaymentScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val latestReceipt by viewModel.latestCreatedReceipt.collectAsState()

    AnimatedContent(
        targetState = latestReceipt,
        transitionSpec = {
            fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
        },
        label = "payment_screen_transition"
    ) { receipt ->
        if (receipt == null) {
            // New Payment Form View
            NewPaymentForm(viewModel = viewModel, modifier = modifier)
        } else {
            // Receipt View
            ReceiptSuccessScreen(viewModel = viewModel, record = receipt, modifier = modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPaymentForm(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val customerName by viewModel.customerName.collectAsState()
    val serviceNumber by viewModel.serviceNumber.collectAsState()
    val billAmount by viewModel.billAmount.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isProcessing by viewModel.isProcessingPayment.collectAsState()
    val lastCollection by viewModel.lastCollectionRecord.collectAsState()

    val context = LocalContext.current

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
                IconButton(onClick = { viewModel.selectTab(AppTab.Home) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryBlue
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "LineMan Pay",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            val avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAxQws3z5heRXKsoBptn6oMQdOrDoBFsLl6OmRDuxFcUZh-IORfHo-Vlh4xNEpsJjNUNkVtA2hnN6Utq-QB94v0QPdsht4iIvgaDNZYc4E0RO8CHL_Jz72zqkI9Wxupyq6Fk8IFlFZNZ9mgjH6Gqn-rh9ADWZEbpyVYMXb8WwbVioY4MD_KZViFiPlbqIanzSZ1yua67JurQFd4V_hCey5aVG5ka7LqHHai0ZjndfORgSNKnpxOzUrDcfpLF_9PuPnCa7fOeQTox_90"
            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, OutlineVariantColor, CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // New Payment title
            Column {
                Text(
                    text = "New Payment",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceColor
                )
                Text(
                    text = "Fill in details to immediately complete the transaction and generate the receipt.",
                    fontSize = 14.sp,
                    color = OnSurfaceVariantColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Form Fields Container Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceLow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Customer Name
                    Column {
                        Text(
                            text = "Customer Name*",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { viewModel.updateCustomerName(it) },
                            placeholder = { Text("e.g. John Doe", color = OutlineVariantColor) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("payment_customer_name"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceLowest,
                                focusedContainerColor = SurfaceLowest,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = OutlineVariantColor
                            ),
                            singleLine = true
                        )
                    }

                    // Service Number
                    Column {
                        Text(
                            text = "Service Number*",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = serviceNumber,
                            onValueChange = { viewModel.updateServiceNumber(it) },
                            placeholder = { Text("12-digit account number", color = OutlineVariantColor) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    Toast.makeText(context, "Scanning QR Code...", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Scanner",
                                        tint = OutlineColor
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("payment_service_number"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceLowest,
                                focusedContainerColor = SurfaceLowest,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = OutlineVariantColor
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    // Bill Amount
                    Column {
                        Text(
                            text = "Bill Amount*",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = billAmount,
                            onValueChange = { viewModel.updateBillAmount(it) },
                            placeholder = { Text("0.00", color = OutlineVariantColor) },
                            leadingIcon = {
                                Text(
                                    text = "₹",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceVariantColor,
                                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("payment_bill_amount"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceLowest,
                                focusedContainerColor = SurfaceLowest,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = OutlineVariantColor
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    // Notes (Optional)
                    Column {
                        Text(
                            text = "Notes (Optional)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { viewModel.updateNotes(it) },
                            placeholder = { Text("Add internal collection notes...", color = OutlineVariantColor) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("payment_notes"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceLowest,
                                focusedContainerColor = SurfaceLowest,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = OutlineVariantColor
                            )
                        )
                    }
                }
            }

            // Contextual Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
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
                        text = "Ensure the Service Number matches the customer's physical bill to avoid processing delays.",
                        fontSize = 14.sp,
                        color = OnSecondaryContainer,
                        lineHeight = 20.sp
                    )
                }
            }

            // Continue Button / Processing Indicator
            Button(
                onClick = { viewModel.processPayment() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("continue_payment_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = customerName.isNotEmpty() && serviceNumber.isNotEmpty() && billAmount.isNotEmpty() && !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Completing Transaction...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Complete Transaction & Receipt", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Arrow",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Aesthetic Bento Grid below
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Last Collection Info
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(112.dp)
                        .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = PrimaryBlue
                        )
                        Column {
                            Text(
                                text = "Last Collection",
                                fontSize = 12.sp,
                                color = OnSurfaceVariantColor
                            )
                            val lastAmtText = if (lastCollection != null) {
                                "${lastCollection?.currency}${String.format("%,.2f", lastCollection?.billAmount)}"
                            } else {
                                "₹0.00"
                            }
                            Text(
                                text = lastAmtText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceColor
                            )
                        }
                    }
                }

                // Security Level Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(112.dp)
                        .border(1.dp, OutlineVariantColor, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = TertiaryColor.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Shield",
                            tint = TertiaryColor
                        )
                        Column {
                            Text(
                                text = "Security Level",
                                fontSize = 12.sp,
                                color = TertiaryColor
                            )
                            Text(
                                text = "High Priority",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TertiaryColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ReceiptSuccessScreen(
    viewModel: MainViewModel,
    record: CollectionRecord,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var printStatus by remember { mutableStateOf("Print Receipt") }
    var isPrinting by remember { mutableStateOf(false) }

    LaunchedEffect(isPrinting) {
        if (isPrinting) {
            printStatus = "Connecting to Printer..."
            kotlinx.coroutines.delay(1200)
            printStatus = "Printing..."
            kotlinx.coroutines.delay(1200)
            printStatus = "Printed Successfully!"
            isPrinting = false
            Toast.makeText(context, "Receipt Printed!", Toast.LENGTH_SHORT).show()
        }
    }

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
                Text(
                    text = "LineMan Pay",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            val avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA7GKfv4SYTY9-LCpnsZwmdAiqbhIwQAuWdjZXTxB6p-KwTtzWqgCFNm-ehHIoqnBJhjDd9nOhiyW3N4-x0QdkkiMNysUbeOA6jP6Hta8lNYrwUf0bdrDMKuwURRvEEQZWOK7kqpCsWsiO0IqeknRii6qy4Z-7ptGEPHS3d7uQnlJoKxJt1uz386E9Dm1AUi_tlLMsUFAJG2SdJuARBBGrMHpglMl4S4Ql-liM_mEr79lYqkskFe2cq7e1VAjLasLNTbZezRybaD1dw"
            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = "Profile",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success Banner
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SuccessContainer)
                    .border(1.dp, SuccessGreen.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Check",
                    tint = SuccessGreen,
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = "Payment Successful",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Transaction completed and funds have been securely processed.",
                fontSize = 14.sp,
                color = OnSurfaceVariantColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Receipt Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("receipt_card")
                    .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                OfficialEasternPowerReceipt(record = record)
            }

            // Print button (Primary)
            Button(
                onClick = { isPrinting = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("print_receipt_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !isPrinting
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Bluetooth, contentDescription = "Bluetooth")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = printStatus, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Share & Done row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Share button
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Sharing receipt details...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("share_receipt_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                    border = BorderStroke(2.dp, PrimaryBlue)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share icon")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Share", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Done button
                Button(
                    onClick = { viewModel.dismissSuccessReceipt() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("done_receipt_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer)
                ) {
                    Text(text = "Done", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSecondaryContainer)
                }
            }

            // Secondary Info Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineVariantColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceHigh),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = OnSurfaceVariantColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Digital copy stored",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceColor
                        )
                        Text(
                            text = "A copy of this receipt has been synced to the master ledger and the customer's registered mobile number via SMS.",
                            fontSize = 14.sp,
                            color = OnSurfaceVariantColor,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String, isMono: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = OnSurfaceVariantColor)
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceColor,
            fontFamily = if (isMono) androidx.compose.ui.text.font.FontFamily.Monospace else androidx.compose.ui.text.font.FontFamily.Default
        )
    }
}
