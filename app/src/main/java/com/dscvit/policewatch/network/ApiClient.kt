package com.dscvit.policewatch.network

class ApiClient(private val api: ApiInterface) : BaseApiClient() {

    suspend fun getUser(idToken: String) = processResponse {
        api.getUser(idToken)
    }
}