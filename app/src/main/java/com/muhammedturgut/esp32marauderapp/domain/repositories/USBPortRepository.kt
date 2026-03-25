package com.muhammedturgut.esp32marauderapp.domain.repositories

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface USBPortRepository {
    suspend fun openUsbPort(activityContext : Context);
    fun getLog() : Flow<String>

    fun  startListeningUsbPort(): Flow<String>
}