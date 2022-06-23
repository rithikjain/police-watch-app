package com.dscvit.policewatch.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscvit.policewatch.models.User
import com.dscvit.policewatch.repository.UserRepository
import com.dscvit.policewatch.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepo: UserRepository) : ViewModel() {

    private val _getUserLiveData: MutableLiveData<Resource<User>> = MutableLiveData()
    val getUserLiveData: LiveData<Resource<User>> get() = _getUserLiveData

    fun getUser(idToken: String) {
        viewModelScope.launch {
            _getUserLiveData.postValue(userRepo.getUser(idToken))
        }
    }

    fun setUserSignedIn(user: User) {
        userRepo.setUserSignedIn(true)
        userRepo.saveUser(user)
    }

    fun isUserSignedIn(): Boolean {
        return userRepo.isUserSignedIn()
    }
}