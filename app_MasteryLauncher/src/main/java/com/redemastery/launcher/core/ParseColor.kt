package com.redemastery.launcher.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun parseMinecraftColorCodes(input: String): AnnotatedString {
    val colorMap = mapOf(
        '0' to Color(0xFF000000),
        '1' to Color(0xFF0000AA),
        '2' to Color(0xFF00AA00),
        '3' to Color(0xFF00AAAA),
        '4' to Color(0xFFAA0000),
        '5' to Color(0xFFAA00AA),
        '6' to Color(0xFFFFAA00),
        '7' to Color(0xFFAAAAAA),
        '8' to Color(0xFF555555),
        '9' to Color(0xFF5555FF),
        'a' to Color(0xFF55FF55),
        'b' to Color(0xFF55FFFF),
        'c' to Color(0xFFFF5555),
        'd' to Color(0xFFFF55FF),
        'e' to Color(0xFFFFFF55),
        'f' to Color(0xFFFFFFFF)
    )

    val boldCode = 'l'
    val resetCode = 'r'

    return buildAnnotatedString {
        var currentColor: Color = Color.Unspecified
        var isBold = false
        var i = 0

        while (i < input.length) {
            if (input[i] == '§' && i + 1 < input.length) {
                val code = input[i + 1].lowercaseChar()
                when (code) {
                    in colorMap.keys -> {
                        currentColor = colorMap[code]!!
                        isBold = false
                    }
                    boldCode -> isBold = true
                    resetCode -> {
                        currentColor = Color.Unspecified
                        isBold = false
                    }
                }
                i += 2
            } else {
                val style = SpanStyle(
                    color = if (currentColor != Color.Unspecified) currentColor else Color.Unspecified,
                    fontWeight = if (isBold) FontWeight.Bold else null
                )
                withStyle(style){
                    append(input[i])
                }
                i++
            }
        }
    }
}
