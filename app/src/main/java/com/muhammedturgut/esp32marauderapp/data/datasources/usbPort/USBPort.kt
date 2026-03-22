package com.muhammedturgut.esp32marauderapp.data.datasources.usbPort

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class USBPort @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext  val context: Context
) {

    companion object {
        private const val ACTION_USB_PERMISSION = "com.muhammedturgut.esp32marauderapp.USB_PERMISSION"
        private const val TAG = "USBPort"
    }

    private var port: UsbSerialPort? = null

    // artık lateinit değil, Hilt context'i inject ettiği için direkt başlatıyoruz
    private val usbManager: UsbManager =
        context.getSystemService(Context.USB_SERVICE) as UsbManager

    private fun setupUsbPort(activityContext : Context) {
        port = openUsbPort(activityContext)
    }

     fun openUsbPort(activityContext : Context): UsbSerialPort? {
        return try {
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            if (availableDrivers.isEmpty()) {
                Log.e(TAG, "Driver bulunamadı")
                return null
            }

            val driver = availableDrivers[0]
            val device = driver.device

            if (!usbManager.hasPermission(device)) {
                Log.e(TAG, "İzin yok")
                Toast.makeText(activityContext, "izin yok! ❌", Toast.LENGTH_SHORT).show()
                return null
            }

            val connection = usbManager.openDevice(device) ?: run {
                Log.e(TAG, "Connection açılamadı")
                Toast.makeText(activityContext, "açılamadı! ❌", Toast.LENGTH_SHORT).show()
                return null
            }

            val port = driver.ports[0]
            port.open(connection)
            port.setParameters(
                9600,
                8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )

            Toast.makeText(activityContext, "Port açıldı! ✅", Toast.LENGTH_SHORT).show()

            Log.d(TAG, "Port başarıyla açıldı")
            port

        } catch (e: Exception) {
            Log.e(TAG, "Port açma hatası: ${e.message}", e)
            null
        }
    }

    // USBPort.kt
    fun requestUsbPermission(activityContext: Context): Boolean {
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)

        if (availableDrivers.isEmpty()) {
            Log.d(TAG, "USB cihaz bulunamadı")
            return false
        }

        val device = availableDrivers[0].device

        return if (usbManager.hasPermission(device)) {
            Log.d(TAG, "İzin var, port açılıyor...")
            port = openUsbPort(activityContext)  // ← bu satırı ekle
            port != null
        } else {
            Log.d(TAG, "İzin yok, isteniyor...")
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE else 0

            val permissionIntent = PendingIntent.getBroadcast(
                activityContext,
                0,
                Intent(ACTION_USB_PERMISSION),
                flags
            )
            usbManager.requestPermission(device, permissionIntent)
            false
        }
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let { setupUsbPort(activityContext = context) }
                    } else {
                        Log.d(TAG, "İzin reddedildi")
                    }
                }
            }
        }
    }

    fun listenToPort(): Flow<String> = flow {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, "Okuma döngüsü başladı", Toast.LENGTH_SHORT).show()
        }

        val buffer = ByteArray(1024)
        var leftover = ""

        while (true) {
            try {
                val len = port?.read(buffer, 1000) ?: break

                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    Toast.makeText(context, "len: $len", Toast.LENGTH_SHORT).show()
                }

                if (len > 0) {
                    val received = String(buffer, 0, len)
                    val combined = leftover + received
                    val lines = combined.split("\n")
                    leftover = lines.last()
                    for (i in 0 until lines.size - 1) {
                        val line = lines[i].trim()
                        if (line.isEmpty()) continue
                        emit(line)
                    }
                }
            } catch (e: Exception) {
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
                }
                break
            }
        }
    }.flowOn(Dispatchers.IO)

    fun sendData(data: String) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, "Gönderildi: $data", Toast.LENGTH_SHORT).show()
        }
        try {
            port?.write(data.toByteArray(), 1000)
        } catch (e: Exception) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                Toast.makeText(context, "Yazma hatası: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}