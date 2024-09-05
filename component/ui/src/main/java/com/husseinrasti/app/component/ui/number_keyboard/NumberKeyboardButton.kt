package com.husseinrasti.app.component.ui.number_keyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun NumberKeyboardButton(
    modifier: Modifier,
    textStyle: TextStyle,
    shape: Shape = RoundedCornerShape(size = 8.dp),
    haptics: HapticFeedback = LocalHapticFeedback.current,
    number: Int,
    listener: NumberKeyboardClickedListener
) {
    TextButton(
        modifier = modifier,
        shape = shape,
        border = BorderStroke(1.dp, Color.LightGray),
        colors = ButtonDefaults.textButtonColors().copy(
            contentColor = Color.LightGray
        ),
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            listener.onNumberClicked(number)
        }
    ) {
        Text(
            text = number.toString(),
            style = textStyle
        )
    }
}

@Composable
fun NumberKeyboardAuxButton(
    modifier: Modifier,
    textStyle: TextStyle,
    tintImage: Color = MaterialTheme.colors.secondaryVariant,
    shape: Shape = RoundedCornerShape(size = 8.dp),
    haptics: HapticFeedback = LocalHapticFeedback.current,
    value: String? = null,
    imageVector: ImageVector? = null,
    clicked: () -> Unit
) {
    if (value.isNullOrBlank() && imageVector == null) return
    TextButton(
        modifier = modifier,
        shape = shape,
        border = BorderStroke(1.dp, Color.LightGray),
        colors = ButtonDefaults.textButtonColors().copy(
            contentColor = Color.LightGray
        ),
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            clicked.invoke()
        }
    ) {
        if (!value.isNullOrBlank()) {
            Text(
                text = value,
                style = textStyle
            )
        } else if (imageVector != null) {
            Image(
                imageVector = imageVector,
                contentDescription = imageVector.name,
                colorFilter = ColorFilter.tint(tintImage)
            )
        }
    }
}

@Composable
fun RowScope.NumberKeyboardBlank() {
    Spacer(modifier = Modifier.weight(1F))
}