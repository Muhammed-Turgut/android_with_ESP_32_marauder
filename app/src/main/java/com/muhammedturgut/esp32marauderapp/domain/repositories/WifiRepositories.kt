package com.muhammedturgut.esp32marauderapp.domain.repositories

import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface WifiRepositories {
    fun getWifiList(): Flow<WifiNetwork>
    fun sendCommand(command: String)
}