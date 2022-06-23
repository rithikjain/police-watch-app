package com.dscvit.policewatch.repository

import android.content.SharedPreferences
import com.dscvit.policewatch.utils.Constants
import com.dscvit.policewatch.utils.PrefHelper.set
import com.dscvit.policewatch.utils.PrefHelper.get

class UserRepository(private val sharedPref: SharedPreferences) {

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