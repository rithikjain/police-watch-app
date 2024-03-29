package com.dscvit.policewatch.ui.supervisor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dscvit.policewatch.models.User
import com.dscvit.policewatch.repository.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupervisorViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    init {
        updateUserToken()
    }

    private var _idToken: MutableLiveData<String> = MutableLiveData()
    val idToken: LiveData<String> get() = _idToken

    fun getUserToken(): String {
        return userRepository.getSavedUserToken()
    }

    fun resetSavedUserToken() {
        userRepository.resetSavedUserToken()
    }

    fun setUserSignedOut() {
        userRepository.setUserSignedIn(false)
    }

    fun getSavedUser(): User? {
        return userRepository.getSavedUser()
    }

    private fun updateUserToken() {
        Firebase.auth.currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                userRepository.saveUserToken(it.result.token ?: "")
                _idToken.postValue(it.result.token ?: "")
            }
        }
    }
}