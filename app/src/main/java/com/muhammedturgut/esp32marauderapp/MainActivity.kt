package com.muhammedturgut.esp32marauderapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.muhammedturgut.esp32marauderapp.core.appNavigation.AppNavHost
import com.muhammedturgut.esp32marauderapp.presentation.viewModel.UsbViewModel
import com.muhammedturgut.esp32marauderapp.ui.theme.Esp32marauderappTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<UsbViewModel>()
    private var port: UsbSerialPort? = null
    private lateinit var usbManager: UsbManager

    companion object {
        private const val ACTION_USB_PERMISSION = "com.muhammedturgut.esp32marauderapp.USB_PERMISSION"
        private const val TAG = "MainActivity"
    }

    // USB izin receiver
    private val usbReceiver = object : BroadcastReceiver() {

        /* Bu bir fonksiyon değil, bir "dinleyicidir".
         Ne yapar? Kullanıcıya "Bu USB cihazına izin veriyor musunuz?" sorusu sorulduğunda, kullanıcının verdiği cevabı (Evet/Hayır) yakalar.
         */

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
                        device?.let {
                            Log.d(TAG, "İzin verildi, port açılıyor...")
                            viewModel.updateStatus("İzin alındı, bağlanıyor...")
                            setupUsbPort()
                        }
                    } else {
                        Log.d(TAG, "İzin reddedildi")
                        viewModel.updateStatus("USB izni reddedildi")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        usbManager = getSystemService(UsbManager::class.java)

        setContent {
            Esp32marauderappTheme {
                val navController = rememberNavController();
                Scaffold { innerPadding ->

                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }

        // USB receiver kaydet
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        }

        // USB izin kontrolü
        requestUsbPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
        port?.close()
    }

    private fun requestUsbPermission() {

    /*
    Bağlantı sürecinin ilk adımıdır.
    Ne yapar?
    1. Telefona takılı uyumlu bir USB cihaz (ESP32) var mı diye bakar.
    2. Eğer varsa ve izin daha önce alınmışsa doğrudan setupUsbPort()'a geçer.
    3. İzin yoksa, ekranda o meşhur "İzin verilsin mi?" penceresini açar.
    */

        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)

        if (availableDrivers.isEmpty()) {
            Log.d(TAG, "USB cihaz bulunamadı")
            viewModel.updateStatus("USB cihaz bulunamadı")
            return
        }

        val driver = availableDrivers[0]
        val device = driver.device

        Log.d(TAG, "USB cihaz bulundu: ${device.deviceName}")
        viewModel.updateStatus("USB cihaz bulundu, izin isteniyor...")

        if (usbManager.hasPermission(device)) {
            Log.d(TAG, "İzin zaten var, port açılıyor...")
            setupUsbPort()
        } else {
            Log.d(TAG, "İzin isteniyor...")
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }
            val permissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(ACTION_USB_PERMISSION),
                flags
            )
            usbManager.requestPermission(device, permissionIntent)
        }
    }

    private fun setupUsbPort() {
        port = openUsbPort()

        port?.let { p ->
            viewModel.setPort(p)
            viewModel.updateStatus("USB bağlı, veri bekleniyor...")

            // Veri okuma coroutine
            lifecycleScope.launch(Dispatchers.IO) {
                val buffer = ByteArray(1024)
                var leftover = ""

                while (isActive) {
                    try {
                        val len = p.read(buffer, 1000)
                        if (len > 0) {
                            val received = String(buffer, 0, len)
                            val combined = leftover + received
                            val lines = combined.split("\n")

                            // Son satır tamamlanmamış olabilir
                            leftover = lines.last()

                            // Tamamlanmış satırları işle
                            for (i in 0 until lines.size - 1) {
                                val line = lines[i].trim()
                                if (line.isEmpty()) continue

                                Log.d(TAG, "Alınan: $line")

                                try {
                                    val json = JSONObject(line)
                                    viewModel.onJsonReceived(json)
                                } catch (e: Exception) {
                                    Log.e(TAG, "JSON parse hatası: ${e.message}")
                                    Log.e(TAG, "Problematik veri: $line")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Port read hatası: ${e.message}")
                        viewModel.updateStatus("Bağlantı hatası: ${e.message}")
                        break
                    }
                }
            }
        } ?: run {
            viewModel.updateStatus("Port açılamadı")
        }
    }

    private fun openUsbPort(): UsbSerialPort? {
        try {
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            if (availableDrivers.isEmpty()) {
                Log.e(TAG, "Driver bulunamadı")
                return null
            }

            val driver = availableDrivers[0]
            val device = driver.device

            if (!usbManager.hasPermission(device)) {
                Log.e(TAG, "İzin yok")
                return null
            }

            val connection = usbManager.openDevice(device)
            if (connection == null) {
                Log.e(TAG, "Connection açılamadı")
                return null
            }

            val port = driver.ports[0]
            port.open(connection)
            port.setParameters(
                115200,
                8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )

            Log.d(TAG, "Port başarıyla açıldı")
            return port

        } catch (e: Exception) {
            Log.e(TAG, "Port açma hatası: ${e.message}", e)
            return null
        }
    }
}