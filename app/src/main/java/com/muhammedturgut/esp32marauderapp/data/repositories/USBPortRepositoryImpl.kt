package com.muhammedturgut.esp32marauderapp.data.repositories

import android.content.Context
import com.muhammedturgut.esp32marauderapp.data.datasources.usbPort.USBPort
import com.muhammedturgut.esp32marauderapp.domain.repositories.USBPortRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class USBPortRepositoryImpl @Inject constructor(
    private val usbPort: USBPort
) : USBPortRepository{
    override suspend fun openUsbPort(activityContext : Context) {
        usbPort.openUsbPort()
    }

    override fun getLog(): Flow<String> {
        return usbPort.debugLogs
    }

    override fun startListeningUsbPort() : Flow<String> {
       return usbPort.listenToPort()
    }

}