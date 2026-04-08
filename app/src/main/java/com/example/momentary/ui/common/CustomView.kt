package com.example.momentary.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

enum class TextFieldType {
    EMAIL,
    PASSWORD,
    TEXT
}

@Composable
fun CustomView(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    type: TextFieldType = TextFieldType.TEXT,
    leadingIcon: Painter? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {}
) {

    val visualTransformation: VisualTransformation
    val trailingIcon: (@Composable () -> Unit)?

    when (type) {

        TextFieldType.PASSWORD -> {
            val passwordVisibleState = remember { mutableStateOf(false) }
            val passwordVisible = passwordVisibleState.value

            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }

            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisibleState.value = !passwordVisibleState.value
                    }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = label
                    )
                }
            }
        }

        TextFieldType.EMAIL -> {
            visualTransformation = VisualTransformation.None

            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = label
                        )
                    }
                }
            }
        }

        else -> {
            visualTransformation = VisualTransformation.None
            trailingIcon = null
        }
    }

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            visualTransformation = visualTransformation,
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        painter = it,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            trailingIcon = trailingIcon
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
    }
}
