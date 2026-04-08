package com.example.momentary.ui.common

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {

    abstract fun asString(context: Context): String

    data class StringResource(
        @param:StringRes val resId: Int
    ) : UiText() {
        override fun asString(context: Context): String =
            context.getString(resId)
    }
}
