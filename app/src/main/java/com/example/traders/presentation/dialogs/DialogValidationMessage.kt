package com.example.traders.presentation.dialogs

enum class DialogValidationMessage(val message: String) {
    IS_VALID(""),
    IS_TOO_LOW("Min value is "),
    IS_TOO_HIGH("Insufficient"),
    IS_EMPTY("Empty field"),
    IS_UNTOUCHED(""),
}