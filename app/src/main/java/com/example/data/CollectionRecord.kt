	package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection_records")
data class CollectionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiptNumber: String,
    val customerName: String,
    val serviceNumber: String,
    val billAmount: Double,
    val rcAmount: Double = 0.0,
    val notes: String = "",
    val status: String, // "Paid", "Pending", "Overdue"
    val timestamp: Long,
    val agentName: String = "Agent #42",
    val transactionId: String,
    val currency: String = "₹" // "฿" or "₹"
)
