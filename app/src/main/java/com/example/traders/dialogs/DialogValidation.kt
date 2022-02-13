package com.example.traders.dialogs

import java.math.BigDecimal
import javax.inject.Inject

// Provide with hilt
class DialogValidation @Inject constructor() {
    fun validate(input: BigDecimal, minVal: BigDecimal, maxVal: BigDecimal): DialogValidationMessage {
        return when {
            input.equals(0) -> {
                DialogValidationMessage.IS_EMPTY
            }
            input > maxVal -> {
                DialogValidationMessage.IS_TOO_HIGH
            }
            input < minVal -> {
                DialogValidationMessage.IS_TOO_LOW
            }
            else -> {
                DialogValidationMessage.IS_VALID
            }
        }
    }
}
