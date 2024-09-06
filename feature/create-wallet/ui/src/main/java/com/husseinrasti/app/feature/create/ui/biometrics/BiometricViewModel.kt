package com.husseinrasti.app.feature.create.ui.biometrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.husseinrasti.app.feature.auth.domain.entity.PasscodeEntity
import com.husseinrasti.app.feature.auth.domain.usecase.SaveBiometricUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.SavePasscodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.husseinrasti.app.feature.create.domain.usecase.GeneratePhrasesUseCase
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel @Inject constructor(
    private val saveBiometricUseCase: SaveBiometricUseCase
) : ViewModel() {
    fun saveBiometric(enabled: Boolean) {
        viewModelScope.launch {
            saveBiometricUseCase.invoke(
                SaveBiometricUseCase.Params(enabled)
            )
        }
    }

}