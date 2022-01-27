package com.example.traders.dialogs

enum class DialogValidationMessage(val message: String) {
    IS_VALID(""),
    IS_TOO_LOW("Min valuo is "),
    IS_TOO_HIGH("Insufficient"),
    IS_EMPTY("Empty field")
}