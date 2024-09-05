package com.husseinrasti.app.feature.wallet.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow<WalletUiState>(WalletUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getWallet()
    }

    private fun getWallet() {
        viewModelScope.launch {
            _uiState.emit(WalletUiState.Loading)
            delay(1000)
            _uiState.emit(WalletUiState.Success("Wallet"))
        }
    }

}


sealed interface WalletUiState {
    data object Loading : WalletUiState
    data object Error : WalletUiState
    data class Success(val title: String) : WalletUiState
}