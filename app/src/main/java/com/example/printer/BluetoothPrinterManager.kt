package com.example.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.data.CollectionRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Handles Bluetooth Classic (SPP) connections to generic ESC/POS thermal
 * receipt printers — the handheld 58mm/80mm printers commonly used by field
 * collection agents. Almost all of these, regardless of brand, expose a
 * standard Serial Port Profile (SPP) socket and accept the same ESC/POS byte
 * commands, so this works across brands without printer-specific SDKs.
 *
 * Many cheap generic printers don't enforce Bluetooth pairing/bonding at
 * all — they accept an "insecure" RFCOMM connection directly, without ever
 * going through Android's Settings > Bluetooth pairing flow. So this manager
 * does two things a plain "connect to a paired device" approach misses:
 *
 *  1. scanDevices() actively discovers nearby devices (not just already
 *     bonded ones) — this is what lets an unpaired printer show up at all.
 *  2. printToDevice() tries an INSECURE socket first (no bonding required),
 *     and only falls back to a secure socket if that fails.
 */
object BluetoothPrinterManager {

    // Standard SPP UUID used by virtually every Bluetooth serial device,
    // including thermal printers.
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    sealed class PrintResult {
        object Success : PrintResult()
        data class Failure(val message: String) : PrintResult()
    }

    data class PairedPrinter(val name: String, val address: String)

    /**
     * Lists already-paired ("bonded") Bluetooth devices. Caller must have
     * already obtained BLUETOOTH_CONNECT (API 31+) before calling this.
     */
    @SuppressLint("MissingPermission")
    fun getBondedDevices(context: Context): List<PairedPrinter> {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = manager?.adapter ?: return emptyList()
        if (!adapter.isEnabled) return emptyList()

        return try {
            adapter.bondedDevices
                ?.map { device: BluetoothDevice ->
                    PairedPrinter(
                        name = device.name ?: "Unknown device",
                        address = device.address
                    )
                }
                ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    fun isBluetoothEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        return manager?.adapter?.isEnabled == true
    }

    /**
     * Actively scans for nearby Bluetooth devices — including ones that
     * have never been paired via Android Settings. This is what makes an
     * unpaired printer (like a generic Seznik/no-name thermal printer)
     * show up at all, since some of these devices never complete a formal
     * OS-level pairing handshake.
     *
     * Emits each newly-found device as it's discovered, then closes once
     * the scan finishes (~12 seconds) or the collecting coroutine is
     * cancelled (e.g. the picker dialog is dismissed).
     *
     * Requires BLUETOOTH_SCAN (API 31+) or ACCESS_FINE_LOCATION (below
     * API 31) to already be granted — check before calling.
     */
    @SuppressLint("MissingPermission")
    fun scanDevices(context: Context): Flow<PairedPrinter> = callbackFlow {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = manager?.adapter
        if (adapter == null || !adapter.isEnabled) {
            close()
            return@callbackFlow
        }

        val seenAddresses = mutableSetOf<String>()
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device ?: return
                        if (seenAddresses.add(device.address)) {
                            trySend(
                                PairedPrinter(
                                    name = device.name ?: "Unknown device",
                                    address = device.address
                                )
                            )
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> close()
                }
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
        )

        // Also emit already-bonded devices immediately — discovery alone
        // sometimes skips devices Android already knows about.
        try {
            adapter.bondedDevices?.forEach { device ->
                if (seenAddresses.add(device.address)) {
                    trySend(PairedPrinter(name = device.name ?: "Unknown device", address = device.address))
                }
            }
        } catch (_: SecurityException) {
            // Ignore — permission not granted, discovery below still runs.
        }

        try {
            adapter.startDiscovery()
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: IllegalArgumentException) {
                // Already unregistered — safe to ignore.
            }
            try {
                adapter.cancelDiscovery()
            } catch (_: SecurityException) {
            }
        }
    }

    /**
     * Connects to the printer at [deviceAddress] and writes [data] to it,
     * then closes the connection. Runs on Dispatchers.IO — safe to call
     * from a coroutine on the main thread.
     *
     * Tries an INSECURE socket first (works with printers that don't
     * enforce Bluetooth authentication/bonding — common with cheap generic
     * thermal printers), then falls back to a SECURE socket if that fails.
     */
    @SuppressLint("MissingPermission")
    suspend fun printToDevice(
        context: Context,
        deviceAddress: String,
        data: ByteArray
    ): PrintResult = withContext(Dispatchers.IO) {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = manager?.adapter
            ?: return@withContext PrintResult.Failure("Bluetooth is not supported on this device.")

        if (!adapter.isEnabled) {
            return@withContext PrintResult.Failure("Bluetooth is turned off. Turn it on and try again.")
        }

        val device: BluetoothDevice = try {
            adapter.getRemoteDevice(deviceAddress)
        } catch (e: IllegalArgumentException) {
            return@withContext PrintResult.Failure("Invalid printer address.")
        }

        adapter.cancelDiscovery() // discovery slows down/can break connection attempts

        // Attempt 1: insecure socket — works even if the printer was never
        // formally paired through Android Settings.
        val insecureResult = tryConnectAndWrite(device, data, insecure = true)
        if (insecureResult == null) return@withContext PrintResult.Success

        // Attempt 2: secure socket — some printers require this instead.
        val secureResult = tryConnectAndWrite(device, data, insecure = false)
        if (secureResult == null) return@withContext PrintResult.Success

        PrintResult.Failure(
            "Could not connect to the printer. Make sure it's powered on and in range. " +
                "(${secureResult.message ?: insecureResult.message ?: "connection failed"})"
        )
    }

    /** Returns null on success, or the exception on failure. */
    @SuppressLint("MissingPermission")
    private fun tryConnectAndWrite(device: BluetoothDevice, data: ByteArray, insecure: Boolean): Exception? {
        var socket: BluetoothSocket? = null
        return try {
            socket = if (insecure) {
                device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
            } else {
                device.createRfcommSocketToServiceRecord(SPP_UUID)
            }
            socket.connect()
            socket.outputStream.apply {
                write(data)
                flush()
            }
            null
        } catch (e: IOException) {
            e
        } catch (e: SecurityException) {
            e
        } finally {
            try {
                socket?.close()
            } catch (e: IOException) {
                // Ignore — socket may already be closed if connect() failed.
            }
        }
    }

    // ---- ESC/POS receipt formatting ----------------------------------

    // Most 58mm thermal printers print ~32 characters per line at the
    // default font size. If your printer is 80mm, change this to 48.
    private const val LINE_WIDTH = 32

    private val INIT = byteArrayOf(0x1B, 0x40) // ESC @ — reset printer state
    private fun align(mode: Int) = byteArrayOf(0x1B, 0x61, mode.toByte()) // 0=left,1=center,2=right
    private fun bold(on: Boolean) = byteArrayOf(0x1B, 0x45, if (on) 1 else 0)
    private val FEED_AND_CUT = byteArrayOf(
        0x0A, 0x0A, 0x0A, 0x0A, // feed a few lines so the cut clears the printed text
        0x1D, 0x56, 0x42, 0x00  // GS V B 0 — partial cut. Harmless no-op on printers without a cutter.
    )

    /**
     * Builds the ESC/POS byte sequence for printing [record] as a receipt,
     * ready to pass into printToDevice(). Mirrors the on-screen
     * OfficialEasternPowerReceipt layout: Bill Amount, RC Amount, and the
     * combined Total Amount.
     */
    fun buildReceiptBytes(record: CollectionRecord): ByteArray {
        val out = ByteArrayOutputStream()

        fun line(text: String) = out.write((text + "\n").toByteArray(Charsets.US_ASCII))
        fun fieldLine(label: String, value: String): String {
            val prefix = "$label :"
            val space = (LINE_WIDTH - prefix.length - value.length).coerceAtLeast(1)
            return prefix + " ".repeat(space) + value
        }

        out.write(INIT)

        out.write(align(1)) // center
        out.write(bold(true))
        line("Eastern power")
        out.write(bold(false))
        line("-".repeat(LINE_WIDTH))
        line("PAYMENT RECEIPT")
        line("-".repeat(LINE_WIDTH))

        out.write(align(0)) // left
        val dateStr = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US).format(Date(record.timestamp))
        line(fieldLine("Receipt No", record.receiptNumber))
        line(fieldLine("Date", dateStr))
        line(fieldLine("Customer", record.customerName))
        line(fieldLine("Service No", record.serviceNumber))
        line("-".repeat(LINE_WIDTH))
        line(fieldLine("Bill Amount", "%.2f".format(record.billAmount)))
        line(fieldLine("RC Amount", "%.2f".format(record.rcAmount)))

        val total = record.billAmount + record.rcAmount
        out.write(bold(true))
        line(fieldLine("Total Amount", "${record.currency}${"%.2f".format(total)}"))
        out.write(bold(false))
        line(fieldLine("Amount Paid", "%.2f".format(total)))
        line("-".repeat(LINE_WIDTH))
        line(fieldLine("Txn ID", record.transactionId))
        line(fieldLine("Agent", record.agentName))

        out.write(align(1)) // center
        line("-".repeat(LINE_WIDTH))
        line("Thank you!")

        out.write(FEED_AND_CUT)

        return out.toByteArray()
    }
}
