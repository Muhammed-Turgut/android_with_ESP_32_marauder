package com.muhammedturgut.esp32marauderapp.data.repositories

import android.util.Log
import android.widget.Toast
import com.muhammedturgut.esp32marauderapp.data.datasources.usbPort.USBPort
import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import com.muhammedturgut.esp32marauderapp.domain.repositories.WifiRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class WifiRepositoriesImpl @Inject constructor(
    private val usbPort : USBPort
) : WifiRepositories {
    override fun getWifiList(): Flow<WifiNetwork> = flow {
        usbPort.listenToPort().collect { line ->
            try {
                // Ham veriyi göster
                withContext(Dispatchers.Main) {
                    Toast.makeText(usbPort.context, "Ham veri: $line", Toast.LENGTH_SHORT).show()
                }

                val json = JSONObject(line)
                val type = json.getString("type")

                if (type == "wifi_result") {
                    val network = WifiNetwork(
                        ssid = json.getString("ssid"),
                        rssi = json.getInt("rssi"),
                    )
                    emit(network)
                }
            } catch (e: Exception) {
                Log.e("USB", "JSON parse hatası: $line")
            }
        }
    }

    override fun sendCommand(command: String) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(usbPort.context, "Repository sendCommand: $command", Toast.LENGTH_SHORT).show()
        }
        usbPort.sendData("$command\n")
    }

}