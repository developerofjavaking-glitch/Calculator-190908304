package com.javaking.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorTest {
    @Test fun precedence() { assertEquals("14", Calculator.evaluate("2+3×4")) }
    @Test fun parentheses() { assertEquals("20", Calculator.evaluate("(2+3)×4")) }
    @Test fun negative() { assertEquals("-6", Calculator.evaluate("2×−3")) }
    @Test fun percent() { assertEquals("10", Calculator.evaluate("200×5%")) }
    @Test fun decimal() { assertEquals("0.3", Calculator.evaluate("0.1+0.2")) }
    @Test(expected = IllegalArgumentException::class)
    fun divideByZero() { Calculator.evaluate("1÷0") }
    @Test(expected = IllegalArgumentException::class)
    fun incomplete() { Calculator.evaluate("2+") }
    @Test(expected = IllegalArgumentException::class)
    fun unmatchedParenthesis() { Calculator.evaluate("(2+3") }
}
