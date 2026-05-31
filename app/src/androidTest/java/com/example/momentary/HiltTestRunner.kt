package com.example.momentary

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom test runner used by Hilt for UI testing.
 * NOTE: Appears as "never used" in the IDE because it is referenced
 * as a string literal in the `build.gradle.kts` (testInstrumentationRunner).
 */
@Suppress("Unused")
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
