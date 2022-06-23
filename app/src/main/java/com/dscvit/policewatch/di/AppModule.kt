package com.dscvit.policewatch.di

import android.content.Context
import android.content.SharedPreferences
import com.dscvit.policewatch.repository.UserRepository
import com.dscvit.policewatch.utils.Constants
import com.dscvit.policewatch.utils.PrefHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        PrefHelper.customPrefs(context, Constants.SHARED_PREF_NAME)

    @Singleton
    @Provides
    fun provideUserRepo(sharedPref: SharedPreferences): UserRepository = UserRepository(sharedPref)
}