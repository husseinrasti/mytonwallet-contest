package com.husseinrasti.app.component.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun PasscodeInput(
    modifier: Modifier = Modifier,
    passcode: String,
    passcodeCount: Int,
    onPasscodeTextChange: (String, Boolean) -> Unit,
    focusRequester: FocusRequester = FocusRequester.Default,
    colorBox: ColorBox = ColorBox(
        fill = MaterialTheme.colors.secondary,
        empty = MaterialTheme.colors.primary,
        border = MaterialTheme.colors.secondary,
    )
) {
    LaunchedEffect(Unit) {
        if (passcode.length > passcodeCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $passcodeCount characters")
        }
    }

    BasicTextField(
        modifier = modifier
            .focusRequester(focusRequester),
        value = TextFieldValue(passcode, selection = TextRange(passcode.length)),
        onValueChange = {
            if (it.text.length <= passcodeCount) {
                onPasscodeTextChange.invoke(it.text, it.text.length == passcodeCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(passcodeCount) { index ->
                    BoxView(
                        index = index,
                        text = passcode,
                        colorBox = colorBox
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    )
}

@Composable
private fun BoxView(
    index: Int,
    text: String,
    colorBox: ColorBox,
) {
    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        else -> text[index].toString()
    }
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(
                if (char.isEmpty()) colorBox.empty
                else colorBox.fill
            )
            .border(
                1.dp,
                colorBox.border,
                CircleShape
            )
            .padding(2.dp)

    )
}

data class ColorBox(
    val fill: Color,
    val empty: Color,
    val border: Color,
)