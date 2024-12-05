package com.kotlin.inmind.application

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kotlin.inmind.R
import com.kotlin.inmind.util.Constants.INMIND_COLORS
import com.kotlin.inmind.util.Util
import com.kotlin.inmind.util.Util.getFromPreferences
import com.kotlin.inmind.util.Util.setToPreferences

class InMind : Application()
{
    var bSplash = false
    var lastColor: Color = Color.Black
    lateinit var selectedColors: MutableList<Color>

    override fun onCreate()
    {
        val defaultColors = listOf(Color(getColor(R.color.im_red)),
            Color(getColor(R.color.im_orange)),
            Color(getColor(R.color.im_yellow)),
            Color(getColor(R.color.im_green)),
            Color(getColor(R.color.im_blue)),
            Color(getColor(R.color.im_purple)),
        )

        selectedColors = defaultColors.toMutableList()
        bSplash = false
        try
        {
            val currentColorsString = getFromPreferences(this,INMIND_COLORS,"")
            val currentColorsList = currentColorsString.split("-")
            currentColorsList.forEachIndexed { index, it ->
                selectedColors[index] = Color(android.graphics.Color.parseColor("#"+it.trim()))
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            selectedColors = Util.loadColorOptions(this).first().toMutableList()
            val selectedColorsString = selectedColors.joinToString("-") {
                String.format("%06X", 0xFFFFFF and it.toArgb())
            }
            setToPreferences(this, INMIND_COLORS, selectedColorsString)
        }

        super.onCreate()
    }
}