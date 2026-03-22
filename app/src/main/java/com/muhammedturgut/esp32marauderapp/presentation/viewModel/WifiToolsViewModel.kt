package com.muhammedturgut.esp32marauderapp.presentation.viewModel

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import com.muhammedturgut.esp32marauderapp.domain.usecases.GetWifiListUseCase
import com.muhammedturgut.esp32marauderapp.domain.usecases.SendCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WifiToolsViewModel @Inject constructor(
    private val getWifiListUseCase: GetWifiListUseCase,
    private val sendCommandUseCase: SendCommandUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _wifiNetworks = MutableStateFlow<List<WifiNetwork>>(emptyList())
    val wifiNetworks = _wifiNetworks.asStateFlow()
    init {
        startListening()
    }

    private fun startListening() {
        viewModelScope.launch {
            showToast("Dinleme başladı")
            getWifiListUseCase.getWifiList()
                .collect { network ->
                    showToast("Ağ geldi: ${network.ssid}")
                    _wifiNetworks.update { currentList ->
                        currentList + network
                    }
                }
        }
    }

    fun startWifiScan() {
        showToast("Komut gönderiliyor")
        sendCommand("WIFI_SCAN_START")
    }

    fun sendCommand(command: String,) {

        viewModelScope.launch {
            sendCommandUseCase.execute(command)
        }
    }
    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    fun stopWifiScan() {
        sendCommand("WIFI_SCAN_STOP")
    }

    fun clearList() {
        _wifiNetworks.update { emptyList() }
    }
}