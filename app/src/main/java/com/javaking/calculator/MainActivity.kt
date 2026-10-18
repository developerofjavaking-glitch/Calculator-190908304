package com.javaking.calculator

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private lateinit var previous: TextView
    private var expression = ""
    private var completed = false
    private val history = arrayListOf<String>()
    private val preferences by lazy { getSharedPreferences("calculator", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        display = findViewById(R.id.display)
        previous = findViewById(R.id.previous)
        expression = savedInstanceState?.getString("expression") ?: ""
        completed = savedInstanceState?.getBoolean("completed") ?: false
        previous.text = savedInstanceState?.getString("previous") ?: ""
        runCatching {
            val stored = JSONArray(preferences.getString("history", "[]"))
            for (index in 0 until stored.length()) history.add(stored.getString(index))
        }
        findViewById<View>(R.id.history).setOnClickListener { showHistory() }
        val keypad = findViewById<LinearLayout>(R.id.keypad)
        val rows = listOf(
            listOf("AC", "(", ")", "⌫"),
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "−"),
            listOf("0", ".", "%", "+"),
            listOf("=")
        )
        for (keys in rows) {
            val row = LinearLayout(this)
            keypad.addView(row, LinearLayout.LayoutParams(-1, dp(64)))
            for (key in keys) {
                val button = MaterialButton(this).apply {
                    text = key
                    textSize = 23f
                    isAllCaps = false
                    cornerRadius = dp(20)
                    insetTop = dp(3)
                    insetBottom = dp(3)
                    minimumWidth = 0
                    setPadding(0, 0, 0, 0)
                    val background = when {
                        key == "=" -> R.color.accent
                        key == "AC" -> R.color.clear
                        key in listOf("÷", "×", "−", "+", "%", "(", ")", "⌫") -> R.color.operator
                        else -> R.color.surface
                    }
                    backgroundTintList = ColorStateList.valueOf(getColor(background))
                    setTextColor(getColor(if (key == "=") R.color.background else R.color.text))
                    contentDescription = when (key) {
                        "AC" -> getString(R.string.clear)
                        "⌫" -> getString(R.string.backspace)
                        "=" -> getString(R.string.equals)
                        "×" -> getString(R.string.multiply)
                        "÷" -> getString(R.string.divide)
                        "−" -> getString(R.string.subtract)
                        "+" -> getString(R.string.add)
                        "%" -> getString(R.string.percent)
                        else -> key
                    }
                    setOnClickListener { press(key) }
                }
                row.addView(button, LinearLayout.LayoutParams(0, -1, 1f).apply {
                    setMargins(dp(3), 0, dp(3), 0)
                })
            }
        }
        render()
    }

    private fun press(key: String) {
        when (key) {
            "AC" -> {
                expression = ""
                previous.text = ""
                completed = false
            }
            "⌫" -> {
                expression = expression.dropLast(1)
                previous.text = ""
                completed = false
            }
            "=" -> {
                if (expression.isEmpty() || completed) return
                try {
                    val result = Calculator.evaluate(expression)
                    val entry = "$expression = $result"
                    history.add(0, entry)
                    while (history.size > 50) history.removeAt(history.lastIndex)
                    saveHistory()
                    previous.text = "$expression ="
                    expression = result
                    completed = true
                } catch (_: IllegalArgumentException) {
                    previous.setText(R.string.invalid_expression)
                }
            }
            else -> {
                if (completed && (key[0].isDigit() || key == "." || key == "(")) expression = ""
                completed = false
                previous.text = ""
                if (expression.length >= 200) return
                when {
                    key == "." -> {
                        val number = expression.takeLastWhile { it.isDigit() || it == '.' }
                        if ('.' !in number) expression += if (number.isEmpty()) "0." else "."
                    }
                    key == "(" -> {
                        if (expression.lastOrNull()?.let { it.isDigit() || it == ')' || it == '%' } == true) expression += "×"
                        expression += key
                    }
                    else -> expression += key
                }
            }
        }
        render()
    }

    private fun render() {
        display.text = expression.ifEmpty { "0" }
        val scroll = findViewById<HorizontalScrollView>(R.id.display_scroll)
        scroll.post { scroll.fullScroll(View.FOCUS_RIGHT) }
    }

    private fun showHistory() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.history)
            .setPositiveButton(R.string.close, null)
            .setNeutralButton(R.string.clear_history) { _, _ ->
                history.clear()
                saveHistory()
            }
        if (history.isEmpty()) dialog.setMessage(R.string.empty_history)
        else dialog.setItems(history.toTypedArray()) { _, index ->
            expression = history[index].substringAfterLast(" = ")
            previous.text = history[index].substringBeforeLast(" = ") + " ="
            completed = true
            render()
        }
        dialog.show()
    }

    private fun saveHistory() {
        preferences.edit().putString("history", JSONArray(history).toString()).apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("expression", expression)
        outState.putString("previous", previous.text.toString())
        outState.putBoolean("completed", completed)
        super.onSaveInstanceState(outState)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
