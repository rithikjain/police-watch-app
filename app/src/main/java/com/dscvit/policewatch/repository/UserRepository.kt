package com.dscvit.policewatch.repository

import android.content.SharedPreferences
import com.dscvit.policewatch.network.ApiClient
import com.dscvit.policewatch.utils.Constants
import com.dscvit.policewatch.utils.PrefHelper.set
import com.dscvit.policewatch.utils.PrefHelper.get

class UserRepository(private val sharedPref: SharedPreferences, private val apiClient: ApiClient) {

    suspend fun getUser(idToken: String) = apiClient.getUser(idToken)

    fun saveUserToken(userToken: String) {
        sharedPref[Constants.SHARED_PREF_USER_TOKEN] = userToken
    }

    fun getSavedUserToken(): String {
        return sharedPref[Constants.SHARED_PREF_USER_TOKEN] ?: ""
    }

    fun resetSavedUserToken() {
        sharedPref[Constants.SHARED_PREF_USER_TOKEN] = ""
    }
}