package com.example.momentary.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momentary.data.repository.MomentaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: MomentaryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state

    fun onPageChanged(page: Int) {
        _state.update {
            it.copy(
                currentPage = page,
                isLastPage = page == 3
            )
        }
    }

    fun finishOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingCompleted()
        }
    }
}
