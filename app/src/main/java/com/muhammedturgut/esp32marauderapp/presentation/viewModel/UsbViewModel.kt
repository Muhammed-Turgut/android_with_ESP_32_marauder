package com.muhammedturgut.esp32marauderapp.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UsbViewModel @Inject constructor() : ViewModel() {

    private val _wifiList = mutableStateListOf<WifiNetwork>()
    val wifiList: List<WifiNetwork> = _wifiList

    private var port: UsbSerialPort? = null

    var status by mutableStateOf("Bağlanıyor...")
        private set

    fun setPort(usbPort: UsbSerialPort) {
        // Bu fonksiyon, cihazın USB bağlantısı fiziksel olarak kurulduğunda çağrılır.
        port = usbPort
        status = "USB bağlandı"
    }

    fun updateStatus(newStatus: String) {
        // Uygulama arayüzündeki durum metnini manuel olarak değiştirmek için kullanılan yardımcı bir fonksiyondur.
        status = newStatus
    }

    fun onJsonReceived(json: JSONObject) {
        //Sınıfın en kritik fonksiyonudur. ESP32'den gelen ham verileri anlamlandırır.
        try {
            when (json.getString("type")) {
                "status" -> {
                    status = json.getString("msg")
                }

                "wifi" -> {
                    val ssid = json.getString("ssid")
                    val rssi = json.getInt("rssi")

                    // Aynı SSID varsa güncelle
                    _wifiList.removeAll { it.ssid == ssid }
                    _wifiList.add(WifiNetwork(ssid, rssi))

                    status = "WiFi ağları alınıyor... (${_wifiList.size})"
                }

                "command" -> {
                    val action = json.getString("action")
                    status = when (action) {
                        "scan_started" -> "Tarama başladı"
                        "scan_stopped" -> "Tarama durduruldu"
                        else -> action
                    }
                }

                "error" -> {
                    status = "Hata: ${json.getString("msg")}"
                }
            }
        } catch (e: Exception) {
            status = "JSON parse hatası: ${e.message}"
        }
    }

    fun sendCommand(cmd: String) {
        //Telefondan ESP32 cihazına komut göndermek için kullanılır (Örneğin: "scanap" komutu
        try {

            port?.write((cmd + "\n").toByteArray(), 1000)
            status = "Komut gönderildi: $cmd"

        } catch (e: Exception) {
            status = "Gönderim hatası: ${e.message}"
        }
    }

    fun clear() {
        //Uygulama belleğindeki geçici verileri temizlemek için kullanılır.
        _wifiList.clear()
        status = "Liste temizlendi"
    }
}