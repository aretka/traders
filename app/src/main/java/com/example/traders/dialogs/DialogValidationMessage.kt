package com.example.traders.dialogs

enum class DialogValidationMessage(val message: String) {
    IS_VALID(""),
    IS_TOO_LOW("Min value is "),
    IS_TOO_HIGH("Insufficient"),
    IS_TOO_HIGH_DEPOSIT("Max deposit value is "),
    IS_EMPTY("Empty field")
}