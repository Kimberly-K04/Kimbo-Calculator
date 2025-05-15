package com.kwamboka.kimbocalculator.ui.theme.screens.calculator

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlin.math.*

val NeonPurple = Color(0xFF6A0DAD)
val DarkPurple = Color(0xFF1B0033)
val NeonLavender = Color(0xFFE0B3FF)

@Composable
fun CalculatorScreen(navController: NavHostController) {
    val orientation = LocalConfiguration.current.orientation
    var input by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        ScientificCalculatorLayout(input, result,
            onInputChange = { input = it },
            onResultChange = { result = it }
        )
    } else {
        BasicCalculatorLayout(input, result,
            onInputChange = { input = it },
            onResultChange = { result = it }
        )
    }
}

@Composable
fun BasicCalculatorLayout(
    input: String,
    result: String,
    onInputChange: (String) -> Unit,
    onResultChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkPurple)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        DisplaySection(input, result)

        // List of button labels for the calculator grid
        val buttons = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "=", "+")
        )

        // Display the buttons grid
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        text = label,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (label) {
                                "=" -> onResultChange(calculate(input))
                                else -> {
                                    if (canAddCharacter(input, label)) {
                                        onInputChange(appendWithMultiplicationFix(input, label))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        // Row with Clear, Backspace, and Ans buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton(
                text = "C",
                modifier = Modifier.weight(1f),
                onClick = {
                    onInputChange("")
                    onResultChange("")
                }
            )
            CalculatorButton(
                text = "⌫",
                modifier = Modifier.weight(1f),
                onClick = {
                    if (input.isNotEmpty()) {
                        onInputChange(input.dropLast(1))
                        onResultChange("")
                    }
                }
            )
            CalculatorButton(
                text = "Ans",
                modifier = Modifier.weight(1f),
                onClick = {
                    if (result.isNotEmpty() && result != "Error") {
                        // Append the last result to the input with multiplication fix
                        onInputChange(appendWithMultiplicationFix(input, result))
                    }
                }
            )
        }
    }
}

@Composable
fun ScientificCalculatorLayout(
    input: String,
    result: String,
    onInputChange: (String) -> Unit,
    onResultChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkPurple)
            .padding(5.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        DisplaySection(input, result)
        val buttons = listOf(
            listOf("sin", "cos", "tan", "log", "ln"),
            listOf("√", "x²", "(", ")", "C"),
            listOf("7", "8", "9", "÷", "π"),
            listOf("4", "5", "6", "×", "^"),
            listOf("1", "2", "3", "-", "e"),
            listOf("0", ".", "=", "+", "%")
        )
        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        text = label,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (label) {
                                "=" -> onResultChange(calculate(input))
                                "C" -> {
                                    onInputChange("")
                                    onResultChange("")
                                }
                                else -> {
                                    if (canAddCharacter(input, label)) {
                                        onInputChange(appendWithMultiplicationFix(input, label))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DisplaySection(input: String, result: String) {
    val displayInput = input
        .replace("*", "×")
        .replace("/", "÷")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = displayInput,
            fontSize = 36.sp,
            color = NeonLavender,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = result.ifEmpty { "" },
            fontSize = 32.sp,
            color = Color.White,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp, bottom = 16.dp),
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(
                color = NeonPurple.copy(alpha = 0.85f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            color = NeonLavender,
            fontFamily = FontFamily.Monospace
        )
    }
}

// Validation to prevent multiple decimals in current number segment
fun canAddCharacter(input: String, newChar: String): Boolean {
    if (newChar == ".") {
        val lastOperatorIndex = input.lastIndexOfAny(charArrayOf('+', '-', '×', '÷', '^', '(', ')'))
        val currentNumber = if (lastOperatorIndex == -1) input else input.substring(lastOperatorIndex + 1)
        if (currentNumber.contains('.')) return false
    }
    return true
}

// Insert multiplication sign where appropriate automatically
fun appendWithMultiplicationFix(input: String, newChar: String): String {
    val lastChar = input.lastOrNull()

    val functionsOrConstants = setOf("π", "e", "sin", "cos", "tan", "log", "ln", "√", "x²")

    if (lastChar != null) {
        if ((lastChar.isDigit() || lastChar == ')') &&
            (newChar == "(" || newChar in functionsOrConstants)
        ) {
            return input + "*" + newChar
        }
        // Also, if last char is a constant or function and new char is a digit or "(" add *
        if ((lastChar.toString() in functionsOrConstants || lastChar == ')') &&
            (newChar[0].isDigit() || newChar == "(")
        ) {
            return input + "*" + newChar
        }
    }
    return input + newChar
}

fun calculate(expression: String): String {
    return try {
        var expr = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
            .replace("√", "sqrt")
            .replace("x²", "^2")
            .replace("%", "/100")

        val result = eval(expr)
        if (result % 1.0 == 0.0) {
            result.toLong().toString() // show integer if no decimal part
        } else {
            result.toString()
        }
    } catch (e: Exception) {
        "Error"
    }
}

fun eval(expr: String): Double {
    val s = expr.replace("\\s".toRegex(), "")
    return parseExpression(s)
}

// Parsing with recursion based on operator precedence

private var pos = -1
private var ch = 0
private lateinit var str: String

private fun nextChar() {
    pos++
    ch = if (pos < str.length) str[pos].code else -1
}

private fun eat(charToEat: Int): Boolean {
    while (ch == ' '.code) nextChar()
    if (ch == charToEat) {
        nextChar()
        return true
    }
    return false
}

private fun parseExpression(s: String): Double {
    str = s
    pos = -1
    nextChar()
    val x = parse()
    if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
    return x
}

private fun parse(): Double {
    var x = parseTerm()
    while (true) {
        when {
            eat('+'.code) -> x += parseTerm() // addition
            eat('-'.code) -> x -= parseTerm() // subtraction
            else -> return x
        }
    }
}

private fun parseTerm(): Double {
    var x = parseFactor()
    while (true) {
        when {
            eat('*'.code) -> x *= parseFactor() // multiplication
            eat('/'.code) -> x /= parseFactor() // division
            else -> return x
        }
    }
}

private fun parseFactor(): Double {
    if (eat('+'.code)) return parseFactor() // unary plus
    if (eat('-'.code)) return -parseFactor() // unary minus

    var x: Double
    val startPos = pos

    when {
        eat('('.code) -> {
            x = parse()
            if (!eat(')'.code)) throw RuntimeException("Missing ')'")
        }
        ch >= '0'.code && ch <= '9'.code || ch == '.'.code -> { // numbers
            while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
            x = str.substring(startPos, pos).toDouble()
        }
        ch >= 'a'.code && ch <= 'z'.code -> { // functions
            while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
            val func = str.substring(startPos, pos)
            x = parseFactor()
            x = when (func) {
                "sqrt" -> sqrt(x)
                "sin" -> sin(Math.toRadians(x)) // degrees to radians
                "cos" -> cos(Math.toRadians(x))
                "tan" -> tan(Math.toRadians(x))
                "log" -> log10(x)
                "ln" -> ln(x)
                else -> throw RuntimeException("Unknown function: $func")
            }
        }
        else -> throw RuntimeException("Unexpected: " + ch.toChar())
    }

    if (eat('^'.code)) x = x.pow(parseFactor()) // exponentiation

    return x
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorScreen(rememberNavController())
}
