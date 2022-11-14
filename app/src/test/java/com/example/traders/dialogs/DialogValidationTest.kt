package com.example.traders.dialogs
//
//import com.example.traders.presentation.dialogs.DialogValidation
//import com.example.traders.presentation.dialogs.DialogValidationMessage
//import org.junit.Before
//import org.junit.Test
//import java.math.BigDecimal
//import kotlin.test.assertEquals
//
//class DialogValidationTest {
//
//    private lateinit var dialogValidation: DialogValidation
//
//    @Before
//    fun setUp() {
//        dialogValidation = DialogValidation()
//    }
//
//    @Test
//    fun onNullInput_returnIS_EMPTY() {
//        val actual = dialogValidation.validate(null, MIN_VAL, MAX_VAL)
//        val expected = DialogValidationMessage.IS_EMPTY
//        assertEquals(expected, actual)
//    }
//
//    @Test
//    fun onValidInput_returnIS_VALID() {
//        val actual1 = dialogValidation.validate(BigDecimal(150.21), MIN_VAL, MAX_VAL)
//        val actual2 = dialogValidation.validate(BigDecimal(10), MIN_VAL, MAX_VAL)
//        val actual3 = dialogValidation.validate(BigDecimal(1000.00), MIN_VAL, MAX_VAL)
//        val expected = DialogValidationMessage.IS_VALID
//
//        assertEquals(expected, actual1)
//        assertEquals(expected, actual2)
//        assertEquals(expected, actual3)
//    }
//
//    @Test
//    fun onTooHighInput_returnIS_TOO_HIGH() {
//        val actual1 = dialogValidation.validate(BigDecimal(12414124.124), MIN_VAL, MAX_VAL)
//        val actual2 = dialogValidation.validate(BigDecimal(1000.00000001), MIN_VAL, MAX_VAL)
//        val expected = DialogValidationMessage.IS_TOO_HIGH
//        assertEquals(expected, actual1)
//        assertEquals(expected, actual2)
//    }
//
//    @Test
//    fun onTooLowInput_returnIS_TOO_LOW() {
//        val actual1 = dialogValidation.validate(BigDecimal(0), MIN_VAL, MAX_VAL)
//        val actual2 = dialogValidation.validate(BigDecimal(9.999999999), MIN_VAL, MAX_VAL)
//        val actual3 = dialogValidation.validate(BigDecimal(5), MIN_VAL, MAX_VAL)
//        val expected = DialogValidationMessage.IS_TOO_LOW
//        assertEquals(expected, actual1)
//        assertEquals(expected, actual2)
//        assertEquals(expected, actual3)
//    }
//
//    companion object {
//        private val MAX_VAL = BigDecimal(1000)
//        private val MIN_VAL = BigDecimal(10)
//    }
//}
