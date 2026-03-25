package com.muhammedturgut.esp32marauderapp.domain.usecases

import com.muhammedturgut.esp32marauderapp.domain.repositories.USBPortRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogUseCase @Inject constructor(
    private  val usbPortRepository: USBPortRepository
){
    fun getLog() : Flow<String>{
        return  usbPortRepository.getLog()
    }
}