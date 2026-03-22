package com.muhammedturgut.esp32marauderapp.domain.usecases

import android.content.Context
import com.muhammedturgut.esp32marauderapp.domain.repositories.PermissionRepository
import javax.inject.Inject

class USBPermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    suspend fun requestUSBPermission (context: Context) : Boolean{
        return permissionRepository.RequestUSBPermission(context= context);
    }
}