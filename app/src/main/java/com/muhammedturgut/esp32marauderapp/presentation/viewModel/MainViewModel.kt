package com.muhammedturgut.esp32marauderapp.presentation.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammedturgut.esp32marauderapp.domain.usecases.USBPermissionUseCase
import com.muhammedturgut.esp32marauderapp.domain.usecases.openUsbPortUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    private val usbPermissionUseCase: USBPermissionUseCase,
    private val usbPortUseCase: openUsbPortUseCase
): ViewModel(){


    fun requestUsbPermission(activityContext: Context) {
        Log.d("deneme","Selamlar nasılsın")
        viewModelScope.launch {
            val granted = usbPermissionUseCase.requestUSBPermission(activityContext)

            if (granted) {
                Toast.makeText(activityContext, "İzin Verildi", Toast.LENGTH_SHORT).show()
                openUsbPort(activityContext)
            } else {
                Toast.makeText(activityContext, "izin verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun openUsbPort(activityContext : Context) {
        usbPortUseCase.openUsbPort(activityContext)
    }
}