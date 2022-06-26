package com.dscvit.policewatch.di

import android.content.Context
import android.content.SharedPreferences
import com.dscvit.policewatch.network.ApiClient
import com.dscvit.policewatch.network.ApiInterface
import com.dscvit.policewatch.repository.UserRepository
import com.dscvit.policewatch.utils.Constants
import com.dscvit.policewatch.utils.PrefHelper
import com.dscvit.policewatch.utils.PrefHelper.get
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        PrefHelper.customPrefs(context, Constants.SHARED_PREF_NAME)

    private fun getOkHttpClient(context: Context): OkHttpClient {
        val httpClient = OkHttpClient.Builder()

        httpClient.connectTimeout(25, TimeUnit.SECONDS)
        httpClient.readTimeout(25, TimeUnit.SECONDS)

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
            val request = requestBuilder.build()
            return@addInterceptor chain.proceed(request)
        }
        return httpClient.build()
    }

    @Singleton
    @Provides
    fun provideApi(@ApplicationContext context: Context): ApiInterface = Retrofit.Builder()
        .baseUrl("https://${Constants.BASE_URL}")
        .addConverterFactory(GsonConverterFactory.create())
        .client(getOkHttpClient(context))
        .build()
        .create(ApiInterface::class.java)


    @Singleton
    @Provides
    fun provideApiClient(api: ApiInterface): ApiClient = ApiClient(api)

    @Singleton
    @Provides
    fun provideUserRepo(sharedPref: SharedPreferences, apiClient: ApiClient): UserRepository =
        UserRepository(sharedPref, apiClient)
}