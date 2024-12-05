package com.kotlin.inmind

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kotlin.inmind.application.InMind
import com.kotlin.inmind.composable.SplashScreen.SplashScreenComposable
import com.kotlin.inmind.ui.theme.inMindTheme
import com.kotlin.inmind.util.Constants.INMIND_COLORS
import com.kotlin.inmind.util.Util.changeActivitySlide
import com.kotlin.inmind.util.Util.setToPreferences

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity()
{
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val inMind = applicationContext as InMind

        setContent()
        {
            inMindTheme()
            {
                Scaffold(modifier = Modifier.fillMaxSize())
                {
                    SplashScreenComposable (
                        onPlayClick = {
                            changeActivitySlide(this, GameActivity::class.java)
                        },
                        onColorWheelSelected = { selectedColors ->
                            saveSelectedColors(selectedColors)
                            inMind.selectedColors = selectedColors.toMutableList()
                        },
                        currentColors = inMind.selectedColors,
                        skipSplash = inMind.bSplash,
                    )
                }
            }
        }
    }

    private fun saveSelectedColors(selectedColors: List<Color>)
    {
        val selectedColorsString = selectedColors.joinToString("-") {
            String.format("%06X", 0xFFFFFF and it.toArgb())
        }
        setToPreferences(this, INMIND_COLORS, selectedColorsString)
    }
}
