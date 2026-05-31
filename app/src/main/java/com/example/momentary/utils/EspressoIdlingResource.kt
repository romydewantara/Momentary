package com.example.momentary.utils

import androidx.test.espresso.idling.CountingIdlingResource
import com.example.momentary.utils.EspressoIdlingResource.decrement
import com.example.momentary.utils.EspressoIdlingResource.increment

object EspressoIdlingResource {

    private const val RESOURCE_NAME = "MOMENTARY_GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE_NAME)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(block: () -> T): T {
    increment()
    return try {
        block()
    } finally {
        decrement()
    }
}
