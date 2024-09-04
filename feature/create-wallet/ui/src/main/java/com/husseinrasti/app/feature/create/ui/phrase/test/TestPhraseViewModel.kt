package com.husseinrasti.app.feature.create.ui.phrase.test

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.husseinrasti.app.feature.create.domain.usecase.FilterPhrasesUseCase
import com.husseinrasti.app.feature.create.ui.phrase.model.EditTextState
import javax.inject.Inject
import kotlin.random.Random

internal const val COUNT_PHRASE_TEST = 3
internal const val FROM_RANDOM = 1
internal const val UNTIL_RANDOM = 24
internal const val INDEX_ZERO = 0
internal const val INDEX_ONE = 1
internal const val INDEX_TWO = 2


@HiltViewModel
class TestPhraseViewModel @Inject constructor(
    private val filterPhrasesUseCase: FilterPhrasesUseCase
) : ViewModel() {

    private val _randomNumbers = MutableStateFlow<Set<Int>>(setOf())
    val randomNumbers = _randomNumbers.asStateFlow()

    private val _autocompletePhrases = MutableStateFlow<TestPhraseUiState>(TestPhraseUiState.Idle)
    val autocompletePhrases = _autocompletePhrases.asStateFlow()

    private val _editTextStates = mutableStateListOf(
        EditTextState(),
        EditTextState(),
        EditTextState(),
    )
    val editTextStates: List<EditTextState> get() = _editTextStates

    init {
        generateRandomNumbers()
    }

    private fun generateRandomNumbers() {
        viewModelScope.launch {
            _randomNumbers.update {
                List(COUNT_PHRASE_TEST) { Random.nextInt(FROM_RANDOM, UNTIL_RANDOM) }.toSet()
            }
        }
    }

    fun updateEditTextState(index: Int, newText: String) {
        _editTextStates[index] = _editTextStates[index].copy(text = newText)
        filterPhrases(index = index, word = newText)
    }

    private fun filterPhrases(index: Int, word: String) {
        viewModelScope.launch {
            filterPhrasesUseCase.invoke(FilterPhrasesUseCase.Params(word)).fold(
                onSuccess = { list ->
                    _autocompletePhrases.update {
                        TestPhraseUiState.Success(
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

}


sealed interface TestPhraseUiState {
    data object Idle : TestPhraseUiState
    data class Success(
        val index: Int,
        val phrases: List<String>
    ) : TestPhraseUiState
}