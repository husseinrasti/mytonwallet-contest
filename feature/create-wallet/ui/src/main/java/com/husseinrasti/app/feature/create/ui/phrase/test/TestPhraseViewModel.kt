package com.husseinrasti.app.feature.create.ui.phrase.test

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.husseinrasti.app.feature.auth.domain.entity.PasscodeEntity
import com.husseinrasti.app.feature.auth.domain.usecase.SaveBiometricUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.SavePasscodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.husseinrasti.app.feature.create.domain.usecase.FilterPhrasesUseCase
import com.husseinrasti.app.feature.create.domain.usecase.MatchPhrasesUseCase
import com.husseinrasti.app.feature.create.ui.navigation.CreateWalletRouter
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
    private val filterPhrasesUseCase: FilterPhrasesUseCase,
    private val savePasscodeUseCase: SavePasscodeUseCase,
    private val saveBiometricUseCase: SaveBiometricUseCase,
    private val matchPhrasesUseCase: MatchPhrasesUseCase,
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

    private val _matchPhrases = MutableSharedFlow<Boolean>()
    val matchPhrases = _matchPhrases.asSharedFlow()

    fun matchPhrases(item: CreateWalletRouter.PhraseTesting) {
        viewModelScope.launch {
            matchPhrasesUseCase.invoke(
                MatchPhrasesUseCase.Params(_editTextStates.map { it.text })
            ).fold(
                onSuccess = {
                    if (it) {
                        savePasscode(
                            is6Digits = item.isUse6Digits,
                            passcode = item.passcode
                        )
                        saveBiometric(item.biometric)
                    }
                    _matchPhrases.emit(it)
                },
                onFailure = {
                    _matchPhrases.emit(false)
                }
            )
        }
    }

    private fun saveBiometric(enabled: Boolean) {
        viewModelScope.launch {
            saveBiometricUseCase.invoke(
                SaveBiometricUseCase.Params(enabled)
            )
        }
    }

    private fun savePasscode(is6Digits: Boolean, passcode: String) {
        viewModelScope.launch {
            savePasscodeUseCase.invoke(
                PasscodeEntity(
                    passcode = passcode,
                    is6Digits = is6Digits
                )
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