package com.digitalsln.stanserhorn.di

import com.digitalsln.stanserhorn.tools.DebugManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDebugManager() = DebugManager()

}
