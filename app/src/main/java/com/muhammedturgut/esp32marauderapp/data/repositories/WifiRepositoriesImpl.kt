package com.muhammedturgut.esp32marauderapp.data.repositories

import android.util.Log
import com.muhammedturgut.esp32marauderapp.data.datasources.usbPort.USBPort
import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import com.muhammedturgut.esp32marauderapp.domain.repositories.WifiRepositories
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiRepositoriesImpl @Inject constructor(
    private val usbPort: USBPort
) : WifiRepositories {

    companion object {
        private const val TAG = "WifiRepo"
    }

    /**
     * 🔥 PAYLAŞILMIŞ FLOW (çok önemli)
     * Tek bir listenToPort çalışır, herkes buradan beslenir
     */
    private val sharedPortFlow: SharedFlow<String> =
        usbPort.listenToPort()
            .shareIn(
                scope = kotlinx.coroutines.GlobalScope, // ⚠️ aşağıda açıklıyorum
                started = SharingStarted.WhileSubscribed(5000),
                replay = 0
            )

    override fun getWifiList(): Flow<WifiNetwork> {
        return sharedPortFlow
            .mapNotNull { line ->
                parseWifi(line)
            }
            .flowOn(kotlinx.coroutines.Dispatchers.IO)
    }

    override fun sendCommand(command: String) {
        usbPort.sendData("$command\n") // ✅ newline şart
    }

    /**
     * 🔥 Parse işlemini ayırdık (temiz kod)
     */
    private fun parseWifi(line: String): WifiNetwork? {
        return try {
            val json = JSONObject(line)

            if (json.optString("type") != "wifi_result") {
                return null
            }

            WifiNetwork(
                ssid = json.optString("ssid", "Unknown"),
                rssi = json.optInt("rssi", -100)
            )

        } catch (e: Exception) {
            Log.e(TAG, "Parse error: $line")
            null
        }
    }
}