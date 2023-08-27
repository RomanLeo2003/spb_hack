package com.my.golftrainer.data.di

import android.content.Context
import android.content.SharedPreferences
import com.my.golftrainer.data.repository.SharedPreferencesRepository
import com.my.golftrainer.data.repository.SharedPreferencesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SharedPreferencesModule {

    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("sp", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSharedPreferencesRepository(
        sharedPref: SharedPreferences,
    ): SharedPreferencesRepository = SharedPreferencesRepositoryImpl(sharedPref)
}

