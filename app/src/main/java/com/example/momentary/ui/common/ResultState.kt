package com.example.momentary.ui.common

sealed class ResultState<out T> {
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: UiText) : ResultState<Nothing>()
}
