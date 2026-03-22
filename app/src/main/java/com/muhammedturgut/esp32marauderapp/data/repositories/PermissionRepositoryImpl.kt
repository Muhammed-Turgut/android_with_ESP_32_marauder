package com.muhammedturgut.esp32marauderapp.data.repositories

import android.content.Context
import com.muhammedturgut.esp32marauderapp.data.datasources.usbPort.USBPort
import com.muhammedturgut.esp32marauderapp.domain.repositories.PermissionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionRepositoryImpl @Inject constructor(
    private val usbPort: USBPort
) : PermissionRepository{

    override suspend fun RequestUSBPermission(context: Context) : Boolean {
        return usbPort.requestUsbPermission(context);
    }

}