package com.muhammedturgut.esp32marauderapp.domain.usecases

import android.content.Context
import com.muhammedturgut.esp32marauderapp.domain.repositories.USBPortRepository
import javax.inject.Inject

class OpenUsbPortUseCase @Inject constructor(
    private val usbPortRepository: USBPortRepository
) {
    suspend fun openUsbPort(activityContext : Context){
        usbPortRepository.openUsbPort(activityContext)
    }
}