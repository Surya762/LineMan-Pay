package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CollectionRecord
import java.text.SimpleDateFormat
import java.util.*

// Direct colors to match the receipt image perfectly
val ReceiptPrimaryBlue = Color(0xFF1E3A8A) // Dark blue for labels & values
val ReceiptRed = Color(0xFFC2410C) // Red/orange for Payment Receipt & dashed lines
val ContactInfoBorderColor = Color(0xFF1E3A8A) // Outline color for contact box
val PaperWhite = Color(0xFFFFFFFF)

@Composable
fun OfficialEasternPowerReceipt(
    record: CollectionRecord,
    modifier: Modifier = Modifier
) {
    // Format the date to DD-MM-YYYY
    val formattedDate = try {
        SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(record.timestamp))
    } catch (e: Exception) {
        "04-07-2026"
    }

    // Ensure the transactionId starts with "PYTMA" to match the JPEG
    val displayTxnId = if (record.transactionId.startsWith("PYTMA")) {
        record.transactionId
    } else {
        // Clean out prefix and format
        val suffix = record.transactionId.replace("TXN_", "").take(12)
        "PYTMA26$suffix"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PaperWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.eastern_power_logo),
                contentDescription = "Eastern power logo",
                modifier = Modifier.height(48.dp)
            )
        }

        // 2. PAYMENT RECEIPT Title with Dashed Lines
        RedDashedLine(modifier = Modifier.padding(vertical = 4.dp))
        
        Text(
            text = "PAYMENT RECEIPT",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ReceiptRed,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
        )
        
        RedDashedLine(modifier = Modifier.padding(vertical = 4.dp))

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Receipt Fields (Left Key with colon, Right Value)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReceiptFieldRow(label = "Transaction No :", value = displayTxnId, isMono = true)
            ReceiptFieldRow(label = "Consumer No :", value = record.serviceNumber, isMono = true)
            ReceiptFieldRow(label = "Date :", value = formattedDate)
            
            // Allow dynamic section if entered in notes/details, else default to KAVITI
            val section = if (record.notes.contains("section:", ignoreCase = true)) {
                record.notes.substringAfter("section:").substringBefore("\n").trim()
            } else {
                "KAVITI"
            }
            ReceiptFieldRow(label = "Section :", value = section)

            val ero = if (record.notes.contains("ero:", ignoreCase = true)) {
                record.notes.substringAfter("ero:").substringBefore("\n").trim()
            } else {
                "ERO-PALASA"
            }
            ReceiptFieldRow(label = "ERO :", value = ero)
            
            ReceiptFieldRow(label = "Received From :", value = record.customerName)
            
            val totalAmount = record.billAmount + record.rcAmount

            ReceiptFieldRow(label = "Bill Amount :", value = String.format("%.2f", record.billAmount))
            ReceiptFieldRow(label = "RC Amount :", value = String.format("%.2f", record.rcAmount))

            HorizontalDivider(color = ReceiptRed.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

            ReceiptFieldRow(
                label = "Total Amount :",
                value = "${record.currency}${String.format("%.2f", totalAmount)}",
                emphasize = true
            )
            ReceiptFieldRow(label = "Amount Paid :", value = String.format("%.2f", totalAmount))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Contact Info Box (Rounded outline, red title, blue text)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, ContactInfoBorderColor, RoundedCornerShape(12.dp))
                .background(PaperWhite, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Contact Info",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReceiptRed
                )
                
                HorizontalDivider(color = ReceiptRed, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ContactFieldRow(label = "Call center (toll free) :", value = "1912")
                    ContactFieldRow(label = "AEE/Operation :", value = "9440812439")
                    ContactFieldRow(label = "Dy.EE/Operation :", value = "9440812400")
                    ContactFieldRow(label = "EE/Operation :", value = "9440907281")
                    ContactFieldRow(label = "AAO/ERO :", value = "9440812446")
                }
            }
        }
    }
}

@Composable
fun RedDashedLine(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawLine(
            color = ReceiptRed,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 3f,
            pathEffect = pathEffect
        )
    }
}

@Composable
fun ReceiptFieldRow(
    label: String,
    value: String,
    isMono: Boolean = false,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (emphasize) 16.sp else 14.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
            color = if (emphasize) ReceiptRed else ReceiptPrimaryBlue
        )
        Text(
            text = value,
            fontSize = if (emphasize) 17.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (emphasize) ReceiptRed else ReceiptPrimaryBlue,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ContactFieldRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = ReceiptPrimaryBlue
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ReceiptPrimaryBlue,
            fontFamily = FontFamily.Monospace
        )
    }
}
