package com.muhammedturgut.esp32marauderapp.core.di

import com.muhammedturgut.esp32marauderapp.data.repositories.PermissionRepositoryImpl
import com.muhammedturgut.esp32marauderapp.data.repositories.USBPortRepositoryImpl
import com.muhammedturgut.esp32marauderapp.data.repositories.WifiRepositoriesImpl
import com.muhammedturgut.esp32marauderapp.domain.repositories.PermissionRepository
import com.muhammedturgut.esp32marauderapp.domain.repositories.USBPortRepository
import com.muhammedturgut.esp32marauderapp.domain.repositories.WifiRepositories
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: WifiRepositoriesImpl
    ): WifiRepositories

    @Binds
    @Singleton
    abstract fun bindUserPermissionRepository(
        impl: PermissionRepositoryImpl
    ): PermissionRepository

    @Binds
    @Singleton
    abstract fun bindUsbPortRepository(
        impl: USBPortRepositoryImpl
    ): USBPortRepository
}