package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale

class CollectionRepository(private val collectionDao: CollectionDao) {
    val allCollections: Flow<List<CollectionRecord>> = collectionDao.getAllCollections()

    suspend fun insert(record: CollectionRecord) {
        collectionDao.insertCollection(record)
    }

    suspend fun deleteById(id: Int) {
        collectionDao.deleteCollectionById(id)
    }

    suspend fun initializeSeedDataIfNeeded() {
        if (collectionDao.getCount() == 0) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val seedRecords = listOf(
                CollectionRecord(
                    receiptNumber = "RCP-8829-X2",
                    customerName = "Somchai Peterson",
                    serviceNumber = "009-281-332",
                    billAmount = 1420.50,
                    status = "Paid",
                    timestamp = dateFormat.parse("2023-10-24 14:15")?.time ?: System.currentTimeMillis(),
                    agentName = "Agent #42",
                    transactionId = "TXN_991022837452",
                    currency = "₹"
                ),
                CollectionRecord(
                    receiptNumber = "RCP-8829-X3",
                    customerName = "Bangkok Condo C",
                    serviceNumber = "772-104-556",
                    billAmount = 8900.00,
                    status = "Paid",
                    timestamp = dateFormat.parse("2023-10-23 11:40")?.time ?: System.currentTimeMillis(),
                    agentName = "Agent #42",
                    transactionId = "TXN_991022837453",
                    currency = "₹"
                ),
                CollectionRecord(
                    receiptNumber = "RCP-8829-X4",
                    customerName = "Patchara L.",
                    serviceNumber = "110-492-881",
                    billAmount = 450.25,
                    status = "Paid",
                    timestamp = dateFormat.parse("2023-10-22 09:12")?.time ?: System.currentTimeMillis(),
                    agentName = "Agent #42",
                    transactionId = "TXN_991022837454",
                    currency = "₹"
                ),
                CollectionRecord(
                    receiptNumber = "RCP-8829-X5",
                    customerName = "Rajesh Kumar",
                    serviceNumber = "ELC-90231",
                    billAmount = 2450.00,
                    status = "Paid",
                    timestamp = dateFormat.parse("2023-10-24 09:12")?.time ?: System.currentTimeMillis(),
                    agentName = "Agent #42",
                    transactionId = "TXN_991022837455",
                    currency = "₹"
                ),
                CollectionRecord(
                    receiptNumber = "RCP-8829-X6",
                    customerName = "Priya Singh",
                    serviceNumber = "WTR-44512",
                    billAmount = 840.00,
                    status = "Pending",
                    timestamp = dateFormat.parse("2023-10-24 10:45")?.time ?: System.currentTimeMillis(),
                    agentName = "Agent #12",
                    transactionId = "TXN_991022837456",
                    currency = "₹"
                ),
                CollectionRecord(
                    receiptNumber = "RCP-8829-X7",
                    customerName = "Amit Verma",
                    serviceNumber = "ELC-88124",
                    billAmount = 5200.00,
                    status = "Paid",
                    timestamp = dateFormat.parse("2023-10-24 11:02")?.time ?: System.currentTimeMillis(),
                    agentName = "Agent #42",
                    transactionId = "TXN_991022837457",
                    currency = "₹"
                ),
                CollectionRecord(
                    receiptNumber = "RCP-8829-X8",
                    customerName = "Sunita L.",
                    serviceNumber = "GAS-11029",
                    billAmount = 1120.00,
                    status = "Overdue",
                    timestamp = dateFormat.parse("2023-10-23 16:55")?.time ?: System.currentTimeMillis(),
                    agentName = "Agent #09",
                    transactionId = "TXN_991022837458",
                    currency = "₹"
                )
            )
            collectionDao.insertAll(seedRecords)
        }
    }
}
