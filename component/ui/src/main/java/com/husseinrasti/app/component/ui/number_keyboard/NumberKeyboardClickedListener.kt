package com.husseinrasti.app.component.ui.number_keyboard

interface NumberKeyboardClickedListener {
    fun onNumberClicked(number: Int)
    fun onLeftAuxButtonClicked()
    fun onRightAuxButtonClicked()
}