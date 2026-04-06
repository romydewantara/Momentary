package com.example.momentary.ui.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momentary.R
import com.example.momentary.data.repository.MomentaryRepository
import com.example.momentary.ui.common.ResultState
import com.example.momentary.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: MomentaryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    private val _event = MutableSharedFlow<UiText>()
    val event = _event.asSharedFlow()

    fun onNameChange(value: String) {
        _state.update {
            it.copy(
                name = value,
                nameError = validateName(value)
            )
        }
    }

    fun onEmailChange(value: String) {
        _state.update {
            it.copy(
                email = value,
                emailError = validateEmail(value)
            )
        }
    }

    fun onPasswordChange(value: String) {
        _state.update {
            it.copy(
                password = value,
                passwordError = validatePassword(value)
            )
        }
    }

    fun register() {
        val current = _state.value
        if (!current.isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val response = repository.register(
                name = current.name,
                email = current.email,
                password = current.password
            )) {

                is ResultState.Success -> {
                    _state.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                }

                is ResultState.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(response.message)
                }
            }
        }
    }

    private fun validateName(name: String): UiText? {
        return if (name.isNotEmpty() && name.length < 3) {
            UiText.StringResource(R.string.error_name)
        } else null
    }

    private fun validateEmail(email: String): UiText? {
        return if (email.isNotEmpty() &&
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            UiText.StringResource(R.string.error_email_invalid)
        } else null
    }

    private fun validatePassword(password: String): UiText? {
        return if (password.isNotEmpty() && password.length < 8) {
            UiText.StringResource(R.string.error_password)
        } else null
    }
}
