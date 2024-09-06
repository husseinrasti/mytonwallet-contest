package com.husseinrasti.app.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.husseinrasti.app.feature.auth.domain.entity.AuthEntity
import com.husseinrasti.app.feature.auth.domain.usecase.CheckAuthenticationUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.GetNumPasscodeDigitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkAuthenticationUseCase: CheckAuthenticationUseCase,
    private val getNumPasscodeDigitsUseCase: GetNumPasscodeDigitsUseCase,
) : ViewModel() {

    private val _state = MutableSharedFlow<AuthState>()
    val state = _state.asSharedFlow()

    private val _stateNumDigits = MutableSharedFlow<Boolean>()
    val stateNumDigits = _stateNumDigits.asSharedFlow()

    init {
        getNumPasscodeDigits()
    }

    private fun getNumPasscodeDigits() {
        viewModelScope.launch {
            getNumPasscodeDigitsUseCase.invoke()
                .fold(
                    onSuccess = {
                        _stateNumDigits.emit(it)
                    },
                    onFailure = {
                        _stateNumDigits.emit(false)
                    }
                )
        }
    }

    fun check(biometric: Boolean?, passcode: String?) {
        viewModelScope.launch {
            _state.emit(AuthState.Loading)
            checkAuthenticationUseCase.invoke(
                AuthEntity(
                    passcode = passcode,
                    biometric = biometric
                )
            ).fold(
                onSuccess = { isGranted ->
                    _state.emit(
                        if (isGranted) AuthState.NavigateToMain
                        else AuthState.Error
                    )
                },
                onFailure = {
                    _state.emit(AuthState.Error)
                }
            )
        }
    }

}


sealed interface AuthState {
    data object Loading : AuthState
    data object NavigateToMain : AuthState
    data object Error : AuthState
}