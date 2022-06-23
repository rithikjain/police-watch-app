package com.dscvit.policewatch.repository

import android.content.SharedPreferences
import com.dscvit.policewatch.models.User
import com.dscvit.policewatch.network.ApiClient
import com.dscvit.policewatch.utils.Constants
import com.dscvit.policewatch.utils.PrefHelper.set
import com.dscvit.policewatch.utils.PrefHelper.get
import com.google.gson.Gson

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

    fun setUserSignedIn(isSignedIn: Boolean) {
        sharedPref[Constants.SHARED_PREF_IS_USER_SIGNED_IN] = isSignedIn
    }

    fun isUserSignedIn(): Boolean {
        return sharedPref[Constants.SHARED_PREF_IS_USER_SIGNED_IN] ?: false
    }

    fun saveUser(user: User) {
        sharedPref[Constants.SHARED_PREF_USER] = Gson().toJson(user)
    }

    fun getSavedUser(): User? {
        val json = sharedPref[Constants.SHARED_PREF_USER] ?: ""
        return try {
            Gson().fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}