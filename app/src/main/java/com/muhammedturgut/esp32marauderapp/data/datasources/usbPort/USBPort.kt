package com.muhammedturgut.esp32marauderapp.data.datasources.usbPort

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class USBPort @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext val context: Context
) {

    companion object {
        private const val ACTION_USB_PERMISSION = "com.muhammedturgut.esp32marauderapp.USB_PERMISSION"
        private const val TAG = "USBPort"
        private const val BAUD_RATE = 115200
    }

    private var port: UsbSerialPort? = null

    private val usbManager: UsbManager =
        context.getSystemService(Context.USB_SERVICE) as UsbManager

    private val _debugLogs = MutableSharedFlow<String>(replay = 50)
    val debugLogs = _debugLogs.asSharedFlow()

    // ✅ Thread-safe write queue
    private val writeQueue = Channel<String>(capacity = Channel.BUFFERED)

    private fun log(msg: String) {
        Log.d(TAG, msg)
        _debugLogs.tryEmit("[${System.currentTimeMillis() % 100000}] $msg")
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            log("Broadcast: ${intent.action}")

            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    val device: UsbDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        }

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        log("✅ Permission granted: ${device?.deviceName}")
                        port = openUsbPort()
                    } else {
                        log("❌ Permission denied")
                    }
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    log("USB detached")
                    closePort()
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }

        ContextCompat.registerReceiver(
            context,
            usbReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        log("USBPort initialized")
    }

    fun openUsbPort(): UsbSerialPort? {
        if (port != null && port?.isOpen == true) {
            log("⚠️ Port zaten açık")
            return port
        }

        return try {
            val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)

            if (drivers.isEmpty()) {
                log("❌ No drivers found")
                return null
            }

            val driver = drivers[0]
            val device = driver.device

            if (!usbManager.hasPermission(device)) {
                log("❌ No permission")
                return null
            }

            val connection = usbManager.openDevice(device)
                ?: return log("❌ Connection failed").let { null }

            val usbPort = driver.ports[0]
            usbPort.open(connection)

            // 🔥 RESET (çok önemli)
            usbPort.dtr = false
            usbPort.rts = false
            Thread.sleep(200)
            usbPort.dtr = true
            usbPort.rts = true

            usbPort.setParameters(
                115200,
                8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )

            port = usbPort // ❗❗ BUNU EKLE

            log("✅ Port opened")
            port

        } catch (e: Exception) {
            log("❌ Open error: ${e.message}")
            null
        }
    }

    fun requestUsbPermission(activityContext: Context): Boolean {
        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)

        if (drivers.isEmpty()) {
            log("❌ No USB device")
            return false
        }

        val device = drivers[0].device

        return if (usbManager.hasPermission(device)) {
            log("Permission already granted")
            port = openUsbPort()
            port != null
        } else {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else
                PendingIntent.FLAG_UPDATE_CURRENT

            val intent = PendingIntent.getBroadcast(
                activityContext,
                0,
                Intent(ACTION_USB_PERMISSION),
                flags
            )

            usbManager.requestPermission(device, intent)
            log("Permission requested")
            false
        }
    }

    fun isPortOpen(): Boolean = port?.isOpen == true

    fun closePort() {
        try {
            port?.close()
            log("Port closed")
        } catch (e: Exception) {
            log("Close error: ${e.message}")
        } finally {
            port = null
        }
    }

    fun listenToPort(): Flow<String> = flow {
        if (port == null) {
            log("❌ Port is null")
            return@flow
        }

        val buffer = ByteArray(1024)
        var leftover = ""

        log("✅ Listening started")

        while (currentCoroutineContext().isActive) {
            try {
                // ✅ PORT CHECK
                if (port == null || !isPortOpen()) {
                    log("Port lost, exiting loop")
                    break
                }

                // ✅ WRITE QUEUE SAFE CONSUMPTION
                while (true) {
                    val cmd = writeQueue.tryReceive().getOrNull() ?: break
                    try {
                        port?.write(cmd.toByteArray(), 1000)
                        log("→ $cmd")
                    } catch (e: Exception) {
                        log("❌ Write error: ${e.message}")
                    }
                }

                // ✅ READ
                val len = port?.read(buffer, 100) ?: break

                if (len > 0) {
                    val received = String(buffer, 0, len, Charsets.UTF_8)

                    val combined = leftover + received

                    // 🔥 CRLF FIX
                    val lines = combined.split("\r\n", "\n")

                    leftover = lines.last()

                    for (i in 0 until lines.size - 1) {
                        val line = lines[i].trim()
                        if (line.isNotEmpty()) {
                            log("← $line")
                            emit(line)
                        }
                    }
                }

            } catch (e: Exception) {
                log("❌ Loop error: ${e.message}")
                break
            }
        }

        log("Listening stopped")
    }.flowOn(Dispatchers.IO)

    fun sendData(data: String) {
        val result = writeQueue.trySend(data)

        if (result.isFailure) {
            log("❌ Queue full, drop: $data")
        } else {
            log("Queued: $data")
        }
    }
}