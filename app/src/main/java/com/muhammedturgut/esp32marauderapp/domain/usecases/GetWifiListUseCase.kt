package com.muhammedturgut.esp32marauderapp.domain.usecases

import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import com.muhammedturgut.esp32marauderapp.domain.repositories.WifiRepositories
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWifiListUseCase @Inject constructor(
    private val wifiRepositories: WifiRepositories
){
    fun getWifiList() : Flow<WifiNetwork>{
        return  wifiRepositories.getWifiList()
    }

}