package com.husseinrasti.app.feature.create.ui.phrase.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.husseinrasti.app.feature.create.domain.usecase.GetPhrasesUseCase
import javax.inject.Inject

@HiltViewModel
class RecoveryPhraseViewModel @Inject constructor(
    private val getPhrasesUseCase: GetPhrasesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecoveryPhraseUiState>(RecoveryPhraseUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getPhrases()
    }

    private fun getPhrases() {
        viewModelScope.launch {
            getPhrasesUseCase().fold(
                onSuccess = { phrases ->
                    _uiState.update { RecoveryPhraseUiState.Success(phrases) }
                },
                onFailure = {
                    _uiState.update { RecoveryPhraseUiState.Error }
                }
            )
        }
    }

}


sealed interface RecoveryPhraseUiState {
    data object Loading : RecoveryPhraseUiState
    data object Error : RecoveryPhraseUiState
    data class Success(val phrases: List<String>) : RecoveryPhraseUiState
}