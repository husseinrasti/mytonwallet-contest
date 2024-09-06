package com.husseinrasti.app.feature.create.ui.passcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.husseinrasti.app.feature.auth.domain.entity.PasscodeEntity
import com.husseinrasti.app.feature.auth.domain.usecase.SavePasscodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.husseinrasti.app.feature.create.domain.usecase.GeneratePhrasesUseCase
import javax.inject.Inject

@HiltViewModel
class PasscodeViewModel @Inject constructor(
    private val savePasscodeUseCase: SavePasscodeUseCase
) : ViewModel() {
    fun savePasscode(is6Digits: Boolean, passcode: String) {
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