package com.example.traders.dialogs

import java.math.BigDecimal

// Provide with hilt
class DialogValidation {
    fun validate(input: BigDecimal, minVal: BigDecimal, maxVal: BigDecimal): DialogValidationMessage {
        return if (input.equals(0)) {
            DialogValidationMessage.IS_EMPTY
        } else if (input > maxVal) {
            DialogValidationMessage.IS_TOO_HIGH
        } else if (input < minVal) {
            DialogValidationMessage.IS_TOO_LOW
        } else {
            DialogValidationMessage.IS_VALID
        }
    }
}