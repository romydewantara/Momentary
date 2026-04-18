package com.example.momentary.utils

fun calculatePageOffset(
    currentPage: Int,
    page: Int,
    offsetFraction: Float
): Float {
    return (currentPage - page) + offsetFraction
}
