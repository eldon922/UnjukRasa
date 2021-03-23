package com.pedulinegeri.unjukrasa.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val isSignedIn = authRepository.getSignedInStatus().asLiveData()

    fun signedIn() {
        viewModelScope.launch {
            authRepository.saveSignedInStatus(true)
        }
    }

    fun signedOut() {
        viewModelScope.launch {
            authRepository.saveSignedInStatus(false)
        }
    }
}