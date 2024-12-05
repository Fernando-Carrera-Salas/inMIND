package com.kotlin.inmind.composable

import androidx.compose.ui.Modifier

object Extension
{
    fun Modifier.applyIf(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
        return if (condition) {
            this.then(modifier(this))
        } else {
            this
        }
    }
}