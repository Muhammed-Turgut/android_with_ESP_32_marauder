package com.muhammedturgut.esp32marauderapp.data.repositories

import android.content.Context
import com.muhammedturgut.esp32marauderapp.data.datasources.usbPort.USBPort
import com.muhammedturgut.esp32marauderapp.domain.repositories.USBPortRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class USBPortRepositoryImpl @Inject constructor(
    private val usbPort: USBPort
) : USBPortRepository{
    override suspend fun openUsbPort(activityContext : Context) {
        usbPort.openUsbPort(activityContext = activityContext)
    }

}