package com.kotlin.inmind.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kotlin.inmind.R

@OptIn(ExperimentalTextApi::class)
val Typography = Typography(
    bodyLarge = TextStyle
    (
        fontFamily = FontFamily
        (
            Font
            (
                R.font.bruno_ace_sc,
                variationSettings = FontVariation.Settings
                (
                    FontVariation.weight(950),
                    FontVariation.width(30f),
                    FontVariation.slant(-6f),
                )
            )
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)