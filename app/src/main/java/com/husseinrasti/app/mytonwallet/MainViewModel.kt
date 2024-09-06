package com.husseinrasti.app.mytonwallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.husseinrasti.app.feature.auth.domain.usecase.GetAuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAuthenticationUseCase: GetAuthenticationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state = _state.asStateFlow()

    init {
        getAuthentication()
    }

    private fun getAuthentication() {
        viewModelScope.launch {
            getAuthenticationUseCase.invoke()
                .fold(
                    onSuccess = { isGranted ->
                        _state.update {
                            if (isGranted) MainState.NavigateToAuth
                            else MainState.NavigateToCreateWallet
                        }
                    },
                    onFailure = {
                        _state.update { MainState.NavigateToCreateWallet }
                    }
                )
        }
    }

}

sealed interface MainState {
    data object Idle : MainState
    data object NavigateToAuth : MainState
    data object NavigateToCreateWallet : MainState
}