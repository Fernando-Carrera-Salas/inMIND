package com.kotlin.inmind.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.kotlin.inmind.R

@Composable
fun inMindTheme(content: @Composable () -> Unit)
{
    MaterialTheme(
        colorScheme = lightColorScheme
        (
            primary = colorResource(R.color.primary),
            secondary = colorResource(R.color.secondary),
            tertiary = colorResource(R.color.tertiary),
        ),
        typography = Typography,
        content = content
    )
}