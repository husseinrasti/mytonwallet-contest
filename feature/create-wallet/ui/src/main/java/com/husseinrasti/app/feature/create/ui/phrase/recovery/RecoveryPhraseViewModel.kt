package com.husseinrasti.app.feature.create.ui.phrase.recovery

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.husseinrasti.app.feature.create.domain.usecase.CheckRecoveryPhrasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.husseinrasti.app.feature.create.domain.usecase.FilterPhrasesUseCase
import com.husseinrasti.app.feature.create.ui.phrase.model.EditTextState
import javax.inject.Inject

internal const val COUNT_PHRASE = 24


@HiltViewModel
class RecoveryPhraseViewModel @Inject constructor(
    private val filterPhrasesUseCase: FilterPhrasesUseCase,
    private val checkRecoveryPhrasesUseCase: CheckRecoveryPhrasesUseCase,
) : ViewModel() {

    private val _autocompletePhrases = MutableStateFlow<AutocompletePhraseUiState>(AutocompletePhraseUiState.Idle)
    val autocompletePhrases = _autocompletePhrases.asStateFlow()

    private val _editTextStates = mutableListOf<EditTextState>().apply {
        repeat(COUNT_PHRASE) {
            add(EditTextState())
        }
    }.toMutableStateList()
    val editTextStates: List<EditTextState> get() = _editTextStates

    fun updateEditTextState(index: Int, newText: String) {
        _editTextStates[index] = _editTextStates[index].copy(text = newText)
        filterPhrases(index = index, word = newText)
    }

    private fun filterPhrases(index: Int, word: String) {
        viewModelScope.launch {
            filterPhrasesUseCase.invoke(FilterPhrasesUseCase.Params(word)).fold(
                onSuccess = { list ->
                    _autocompletePhrases.update {
                        AutocompletePhraseUiState.Success(
                            index = index,
                            phrases = list
                        )
                    }
                },
                onFailure = {

                }
            )
        }
    }

    private val _recoveryUiState = MutableSharedFlow<RecoveryPhraseUiState?>()
    val recoveryUiState = _recoveryUiState.asSharedFlow()

    fun checkPhrase() {
        viewModelScope.launch {
            checkRecoveryPhrasesUseCase.invoke(
                CheckRecoveryPhrasesUseCase.Params(editTextStates.map { it.text })
            ).fold(
                onSuccess = {
                    _recoveryUiState.emit(RecoveryPhraseUiState.Success)
                },
                onFailure = {
                    _recoveryUiState.emit(RecoveryPhraseUiState.Error)
                }
            )
        }
    }

    fun setRecoveryUiStateNull() {
        viewModelScope.launch {
            _recoveryUiState.emit(null)
        }
    }

}


sealed interface AutocompletePhraseUiState {
    data object Idle : AutocompletePhraseUiState
    data class Success(
        val index: Int,
        val phrases: List<String>
    ) : AutocompletePhraseUiState
}

sealed interface RecoveryPhraseUiState {
    data object Idle : RecoveryPhraseUiState
    data object Success : RecoveryPhraseUiState
    data object Error : RecoveryPhraseUiState
}