package com.dscvit.policewatch.network

import com.dscvit.policewatch.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiInterface {

    @GET("/users/me")
    suspend fun getUser(@Header("Authorization") idToken: String): Response<User>
}