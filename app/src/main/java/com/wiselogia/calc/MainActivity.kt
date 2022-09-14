package com.wiselogia.calc

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.text.isDigitsOnly
import com.wiselogia.calc.databinding.ActivityMainBinding
import expression.generic.GenericTabulator
import kotlin.math.exp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val ans = GenericTabulator<Double>()
    private lateinit var clipboard: ClipboardManager
    private lateinit var clipData: ClipData
    private var brackets = false
    private var cleanExpression = false
    private var dots = 0
    private var expression: String = ""
        set(value) {
            field = value
            binding.expressionView.text = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        setContentView(binding.root)
        binding.button0.setKeyboardClick("0")
        binding.button1.setKeyboardClick("1")
        binding.button2.setKeyboardClick("2")
        binding.button3.setKeyboardClick("3")
        binding.button4.setKeyboardClick("4")
        binding.button5.setKeyboardClick("5")
        binding.button6.setKeyboardClick("6")
        binding.button7.setKeyboardClick("7")
        binding.button8.setKeyboardClick("8")
        binding.button9.setKeyboardClick("9")
        binding.buttonPlus.setKeyboardClick("+")
        binding.buttonMinus.setKeyboardClick("-")
        binding.buttonMultiply.setKeyboardClick("*")
        binding.buttonDivide.setKeyboardClick("/")
        binding.buttonDot.setKeyboardClick(".")
        binding.buttonC.setKeyboardClickClear()
        binding.buttonEqual.setKeyboardClickCalculate()
        binding.buttonDelete.setKeyboardClickDelete()
        binding.buttonCopy.setKeyboardClickCopy()
        binding.buttonBrackets.setKeyboardClickBracket()
    }

    private fun cleaner() {
        if (cleanExpression) {
            expression = ""
            cleanExpression = false
        }
    }

    private fun View.setKeyboardClick(value: String) {
        this.setOnClickListener {
            cleaner()

            if(value == "." && dots == 0) {
                dots++
                expression += value
            }
            else if(value.isDigitsOnly()) {
                expression += value
            }
            else if(!value.isDigitsOnly() && value != ".") {
                dots = 0
                expression += value
            }
        }
    }

    private fun View.setKeyboardClickBracket() {
        this.setOnClickListener {
            cleaner()
            if (brackets) {
                expression += ")"
                brackets = false
            } else {
                expression += "("
                brackets = true
            }
        }
    }

    private fun View.setKeyboardClickClear() {
        this.setOnClickListener {
            expression = ""
            dots = 0
        }
    }

    private fun View.setKeyboardClickDelete() {
        this.setOnClickListener {
            if (expression.isNotEmpty()) {
                if (expression.last() == '(')
                    brackets = false
                else if (expression.last() == ')')
                    brackets = true
                else if(expression.last() == '.')
                    dots--
                expression = expression.dropLast(1)
            }
        }
    }

    private fun View.setKeyboardClickCopy() {
        this.setOnClickListener {
            clipData = ClipData.newPlainText(expression, expression)
            clipboard.setPrimaryClip(clipData)
        }
    }

    private fun View.setKeyboardClickCalculate() {
        this.setOnClickListener {
            try {
                expression = ans.tabulate("d", expression, 0, 0, 0, 0, 0, 0)[0][0][0].toString()
                dots = 1
            } catch (e: Exception) {
                expression = "err"
                cleanExpression = true
            }
        }
    }

    override fun onSaveInstanceState(
        outState: Bundle
    ) {
        super.onSaveInstanceState(outState)
        outState.putString("maybe baby", expression)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        expression = savedInstanceState.getString("maybe baby").toString()
    }
}