package com.muhammedturgut.esp32marauderapp.domain.repositories

import android.content.Context

interface USBPortRepository {
    suspend fun openUsbPort(activityContext : Context);
}