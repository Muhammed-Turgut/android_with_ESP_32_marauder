package com.muhammedturgut.esp32marauderapp.presentation.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammedturgut.esp32marauderapp.domain.usecases.LogUseCase
import com.muhammedturgut.esp32marauderapp.domain.usecases.StartListeningUsbPortUseCase
import com.muhammedturgut.esp32marauderapp.domain.usecases.USBPermissionUseCase
import com.muhammedturgut.esp32marauderapp.domain.usecases.OpenUsbPortUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    private val usbPermissionUseCase: USBPermissionUseCase,
    private val usbPortUseCase: OpenUsbPortUseCase,
    private val logUseCase: LogUseCase,
    private val startListeningUsbPort: StartListeningUsbPortUseCase
): ViewModel(){

    private val _debugLogs = MutableStateFlow<List<String>>(emptyList())
    val debugLogs = _debugLogs.asStateFlow()

    init {
        collectDebugLogs()
    }

    fun requestUsbPermission(activityContext: Context) {

        viewModelScope.launch {
            val granted = usbPermissionUseCase.requestUSBPermission(activityContext)

            if (granted) {
                Toast.makeText(activityContext, "İzin Verildi", Toast.LENGTH_SHORT).show()
                openUsbPort(activityContext)
                //startListening()
            } else {
                Toast.makeText(activityContext, "izin verilmedi", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun startListening() {
        viewModelScope.launch {
            startListeningUsbPort.listenPort()
                .buffer(100)    // ← EKLE
                .conflate()     // ← EKLE
                .collect { line ->
                    _debugLogs.update {
                        (listOf(line) + it).take(50)
                    }
                }
        }
    }


    suspend fun openUsbPort(activityContext : Context) {
        usbPortUseCase.openUsbPort(activityContext)
    }

    private fun collectDebugLogs() {
        viewModelScope.launch {
            logUseCase.getLog()
                .buffer(100)
                .conflate()
                .collect { log ->
                _debugLogs.update {
                    (listOf(log) + it).take(50)
                }
            }
        }
    }
}