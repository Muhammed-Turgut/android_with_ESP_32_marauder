package com.muhammedturgut.esp32marauderapp.domain.usecases

import com.muhammedturgut.esp32marauderapp.domain.repositories.WifiRepositories
import javax.inject.Inject

class SendCommandUseCase @Inject constructor(
    private val wifiRepositories: WifiRepositories
) {
    suspend fun execute(command : String){
        wifiRepositories.sendCommand(command)
    }
}