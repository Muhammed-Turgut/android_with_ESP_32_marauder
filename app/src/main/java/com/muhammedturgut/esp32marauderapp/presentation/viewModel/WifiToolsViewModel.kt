package com.muhammedturgut.esp32marauderapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammedturgut.esp32marauderapp.data.datasources.usbPort.USBPort
import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import com.muhammedturgut.esp32marauderapp.domain.usecases.GetWifiListUseCase
import com.muhammedturgut.esp32marauderapp.domain.usecases.SendCommandUseCase
import com.muhammedturgut.esp32marauderapp.domain.usecases.StartListeningUsbPortUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WifiToolsViewModel @Inject constructor(
    private val getWifiListUseCase: GetWifiListUseCase,
    private val sendCommandUseCase: SendCommandUseCase,
    private val startListeningUsbPortUseCase: StartListeningUsbPortUseCase,
    private val usbPort: USBPort
) : ViewModel() {

    // 🔥 Map kullanıyoruz → duplicate engellemek için
    private val wifiMap = mutableMapOf<String, WifiNetwork>()

    private val _wifiNetworks = MutableStateFlow<List<WifiNetwork>>(emptyList())
    val wifiNetworks = _wifiNetworks.asStateFlow()

    private val _debugLogs = MutableStateFlow<List<String>>(emptyList())
    val debugLogs = _debugLogs.asStateFlow()

    init {
        collectDebugLogs()
        startListening()
    }

    private fun collectDebugLogs() {
        viewModelScope.launch {
            startListeningUsbPortUseCase.listenPort() .collect { log ->
                _debugLogs.update {
                    (listOf(log) + it).take(50)
                }
            }
        }
    }

    private fun startListening() {
        viewModelScope.launch {
            getWifiListUseCase.getWifiList()
                .collect { network ->

                    // 🔥 Aynı SSID varsa güncelle
                    wifiMap[network.ssid] = network

                    // 🔥 Listeyi güncelle (sorted opsiyonel)
                    _wifiNetworks.value = wifiMap.values
                        .sortedByDescending { it.rssi }
                }
        }
    }

    fun startWifiScan() {
        if (!usbPort.isPortOpen()) {
            addLog("❌ Port kapalı, komut gönderilemedi")
            return
        }

        wifiMap.clear() // 🔥 eski listeyi temizle
        _wifiNetworks.value = emptyList()

        sendCommand("WIFI_SCAN_START")
    }

    fun stopWifiScan() {
        sendCommand("WIFI_SCAN_STOP")
    }

    fun sendCommand(command: String) {
        viewModelScope.launch {
            sendCommandUseCase.execute(command)
        }
    }

    fun clearList() {
        wifiMap.clear()
        _wifiNetworks.value = emptyList()
    }

    private fun addLog(msg: String) {
        _debugLogs.update {
            (listOf(msg) + it).take(50)
        }
    }
}