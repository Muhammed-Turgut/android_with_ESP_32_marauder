package com.muhammedturgut.esp32marauderapp.domain.repositories

import android.content.Context
interface PermissionRepository {
    suspend fun RequestUSBPermission(context: Context) : Boolean
}