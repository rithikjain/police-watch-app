package com.dscvit.policewatch.network

import com.dscvit.policewatch.BuildConfig
import com.dscvit.policewatch.utils.Resource
import org.json.JSONObject
import retrofit2.Response

open class BaseApiClient {

    protected suspend fun <T> processResponse(request: suspend () -> Response<T>): Resource<T> {
        try {
            val response = request()
            return if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body, response.code())
                } else {
                    Resource.Error("Server error :(", response.code())
                }
            } else {
                val errorBody = JSONObject(response.errorBody()!!.charStream().readText())
                if (BuildConfig.DEBUG) {
                    Resource.Error(
                        "${response.code()}: ${errorBody.getString("detail")}",
                        response.code()
                    )
                } else {
                    Resource.Error("Error: ${errorBody.getString("detail")} :(", response.code())
                }
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: e.toString()
            return if (BuildConfig.DEBUG) {
                Resource.Error("Network called failed with message $errorMessage", -1)
            } else {
                Resource.Error("Check your internet connection pls!", -1)
            }
        }
    }
}