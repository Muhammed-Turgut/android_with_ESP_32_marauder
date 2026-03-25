package com.muhammedturgut.esp32marauderapp.domain.usecases

import com.muhammedturgut.esp32marauderapp.domain.repositories.USBPortRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StartListeningUsbPortUseCase @Inject constructor(
    private val usbPortRepository: USBPortRepository
) {
    fun listenPort(): Flow<String> = usbPortRepository.startListeningUsbPort()
}