package com.example.traders.dialogs

import java.math.BigDecimal
import javax.inject.Inject

// Provide with hilt
class DialogValidation @Inject constructor() {
    fun validate(input: BigDecimal?, minVal: BigDecimal, maxVal: BigDecimal): DialogValidationMessage {
        return when {
            input == null -> {
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

/*
* Returns string without invalid characters
* Cases:
* 1. First dot returns without dot
* 2. Digits with 2 dots removes 1 dot
* 3. More digits after dot than allowed removes excess values
* */
fun String.validateChars(digitsAfterDot: Int = 2): String {
    if(this.isBlank()) {
        return this
    } else if (this.first() == '.') {

        // first dot not allowed
        return this.replaceFirst(".", "", true)
    } else {
        var numOfDots = 0
        this.forEach { if(it=='.') numOfDots++ }
        if (numOfDots >= 2) {
            // 2 dots case
            return this.reversed().replaceFirst(".", "").reversed()
        } else {
            val reversedIndex = this.reversed().indexOf(".")
            val index = this.indexOf(".")
            // too many digits after dot case
            if(reversedIndex > digitsAfterDot) {
                return this.substring(0, index + digitsAfterDot + 1)
            }
        }
    }
    // in case correct value
    return this
}