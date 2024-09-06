package com.husseinrasti.app.feature.auth.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.husseinrasti.app.feature.auth.domain.entity.AuthEntity
import com.husseinrasti.app.feature.auth.domain.usecase.CheckAuthenticationUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.GetNumPasscodeDigitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkAuthenticationUseCase: CheckAuthenticationUseCase,
    private val getNumPasscodeDigitsUseCase: GetNumPasscodeDigitsUseCase,
) : ViewModel() {

    private val _stateAuth = MutableSharedFlow<AuthState>()
    val stateAuth = _stateAuth.asSharedFlow()

    private val _stateNumDigits = MutableStateFlow(false)
    val stateNumDigits = _stateNumDigits.asStateFlow()

    init {
        getNumPasscodeDigits()
    }

    private fun getNumPasscodeDigits() {
        viewModelScope.launch {
            getNumPasscodeDigitsUseCase.invoke()
                .fold(
                    onSuccess = { isUse6Digits ->
                        Log.i("TAG", "getNumPasscodeDigits: $isUse6Digits")
                        _stateNumDigits.update { isUse6Digits }
                    },
                    onFailure = {}
                )
        }
    }

    fun check(biometric: Boolean?, passcode: String?) {
        Log.i("TAG", "check biometric: $biometric")
        Log.i("TAG", "check passcode: $passcode")
        viewModelScope.launch {
            _stateAuth.emit(AuthState(loading = true))
            checkAuthenticationUseCase.invoke(
                AuthEntity(
                    passcode = passcode,
                    biometric = biometric
                )
            ).fold(
                onSuccess = { isGranted ->
                    _stateAuth.emit(AuthState(navigateToMain = isGranted))
                },
                onFailure = {
                    _stateAuth.emit(AuthState(error = it.message))
                }
            )
        }
    }

}


data class AuthState(
    val loading: Boolean = false,
    val navigateToMain: Boolean = false,
    val error: String? = null,
)
