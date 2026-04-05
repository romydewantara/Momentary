package com.example.momentary.ui.screen.auth.login

import com.example.momentary.ui.common.UiText

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
) {
    val isValid: Boolean
        get() = emailError == null &&
                passwordError == null &&
                email.isNotBlank() &&
                password.length >= 8
}
