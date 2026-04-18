package com.example.momentary.ui.screen.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momentary.data.repository.MomentaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: MomentaryRepository
) : ViewModel() {

    var state by mutableStateOf<SplashUiState?>(null)
        private set

    init {
        viewModelScope.launch {
            val isLoggedIn = repository.isLoggedIn()
            val isOnboardingCompleted = repository.isOnboardingCompleted().first()

            state = SplashUiState(
                isLoggedIn = isLoggedIn,
                isOnboardingCompleted = isOnboardingCompleted
            )
        }
    }
}
