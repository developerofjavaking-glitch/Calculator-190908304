package com.javaking.calculator

import java.math.BigDecimal
import java.math.MathContext

object Calculator {
    fun evaluate(expression: String): String {
        require(expression.length <= 300)
        val value = Parser(expression).parse()
        require(value.isFinite())
        return BigDecimal.valueOf(value).round(MathContext(12)).stripTrailingZeros().toPlainString()
    }

    private class Parser(private val input: String) {
        private var position = 0
        private fun take(char: Char): Boolean {
            if (position < input.length && input[position] == char) {
                position++
                return true
            }
            return false
        }

        fun parse(): Double {
            val result = sum()
            require(position == input.length)
            return result
        }

        private fun sum(): Double {
            var value = product()
            while (true) {
                value = when {
                    take('+') -> value + product()
                    take('−') || take('-') -> value - product()
                    else -> return value
                }
            }
        }

        private fun product(): Double {
            var value = unary()
            while (true) {
                value = when {
                    take('×') || take('*') -> value * unary()
                    take('÷') || take('/') -> {
                        val divisor = unary()
                        require(divisor != 0.0)
                        value / divisor
                    }
                    else -> return value
                }
            }
        }

        private fun unary(): Double = when {
            take('+') -> unary()
            take('−') || take('-') -> -unary()
            else -> atom()
        }

        private fun atom(): Double {
            var value = if (take('(')) {
                val nested = sum()
                require(take(')'))
                nested
            } else {
                val start = position
                while (position < input.length && (input[position].isDigit() || input[position] == '.')) position++
                require(position > start)
                input.substring(start, position).toDouble()
            }
            while (take('%')) value /= 100.0
            return value
        }
    }
}
