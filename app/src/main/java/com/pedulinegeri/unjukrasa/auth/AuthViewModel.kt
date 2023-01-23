package com.pedulinegeri.unjukrasa.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pedulinegeri.unjukrasa.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val isSignedIn = authRepository.getSignedInStatus().asLiveData()
    var uid = ""
    var name = ""

    init {
        viewModelScope.launch {
            authRepository.getUid().collect {
                uid = it
            }
        }

        viewModelScope.launch {
            authRepository.getName().collect {
                name = it
            }
        }
    }

    fun signedIn(uid: String) {
        viewModelScope.launch {
            authRepository.saveSignedInStatus(true)
            authRepository.saveUid(uid)
        }
    }

    fun signedOut() {
        viewModelScope.launch {
            authRepository.saveSignedInStatus(false)
        }
    }

    fun saveName(name: String) {
        viewModelScope.launch {
            authRepository.saveName(name)
        }
    }
}