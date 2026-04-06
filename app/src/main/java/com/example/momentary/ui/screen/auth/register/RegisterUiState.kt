package com.example.momentary.ui.screen.auth.register

import com.example.momentary.ui.common.UiText

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() &&
                password.length >= 8 &&
                nameError == null &&
                emailError == null &&
                passwordError == null
}
