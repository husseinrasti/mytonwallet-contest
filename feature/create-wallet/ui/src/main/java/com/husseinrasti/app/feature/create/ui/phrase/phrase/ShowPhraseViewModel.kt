package com.husseinrasti.app.feature.create.ui.phrase.phrase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.husseinrasti.app.feature.create.domain.usecase.GetPhrasesUseCase
import javax.inject.Inject

@HiltViewModel
class ShowPhraseViewModel @Inject constructor(
    private val getPhrasesUseCase: GetPhrasesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShowPhraseUiState>(ShowPhraseUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getPhrases()
    }

    private fun getPhrases() {
        viewModelScope.launch {
            getPhrasesUseCase().fold(
                onSuccess = { phrases ->
                    _uiState.update { ShowPhraseUiState.Success(phrases) }
                },
                onFailure = {
                    _uiState.update { ShowPhraseUiState.Error }
                }
            )
        }
    }

}


sealed interface ShowPhraseUiState {
    data object Loading : ShowPhraseUiState
    data object Error : ShowPhraseUiState
    data class Success(val phrases: List<String>) : ShowPhraseUiState
}