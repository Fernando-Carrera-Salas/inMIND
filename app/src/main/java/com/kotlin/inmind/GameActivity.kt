package com.kotlin.inmind

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.kotlin.inmind.application.InMind
import com.kotlin.inmind.composable.GameScreen.GameScreenComposable
import com.kotlin.inmind.ui.theme.inMindTheme
import com.kotlin.inmind.util.Constants.INMIND_LEVEL
import com.kotlin.inmind.util.Constants.INMIND_PROGRESS
import com.kotlin.inmind.util.Constants.SLIDE_DIRECTION_LEFT
import com.kotlin.inmind.util.GameHelper.generateGame
import com.kotlin.inmind.util.GameHelper.generateGameConfig
import com.kotlin.inmind.util.GameHelper.maxProgressForLevel
import com.kotlin.inmind.util.Util.changeActivitySlide
import com.kotlin.inmind.util.Util.getFromPreferences
import com.kotlin.inmind.util.Util.reduceSaturationToMinimum
import com.kotlin.inmind.util.Util.setToPreferences

class GameActivity : ComponentActivity()
{
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val inMind = applicationContext as InMind
        inMind.bSplash = true

        val background = inMind.selectedColors.filter { it != inMind.lastColor }.random()
        inMind.lastColor = background

        val currentLevel = getFromPreferences(this, INMIND_LEVEL,1)

        var currentProgress = getFromPreferences(this, INMIND_PROGRESS,0)
        val maxProgress = maxProgressForLevel(currentLevel)
        if (currentProgress>=maxProgress)
            currentProgress = maxProgress - 10

        val gameConfig = generateGameConfig(level = currentLevel,
            currentProgress = currentProgress,
            maxProgress = maxProgress,
            nextMaxProgress = maxProgressForLevel(currentLevel+1)
        )

        val game = generateGame(gameConfig,inMind.selectedColors)

        setContent()
        {
            inMindTheme()
            {
                Scaffold(modifier = Modifier.fillMaxSize())
                {
                    GameScreenComposable(
                        onBack = {
                            changeActivitySlide(this,SplashActivity::class.java, SLIDE_DIRECTION_LEFT)
                        },
                        selectedColors = inMind.selectedColors,
                        game = game,
                        nextGame = {
                            changeActivitySlide(this,GameActivity::class.java)
                        },
                        background = reduceSaturationToMinimum(background,0.1f),
                        levelProgress = {progress, level ->
                            setToPreferences(this, INMIND_PROGRESS, progress)
                            setToPreferences(this, INMIND_LEVEL, level)
                        },
                    )
                }
            }
        }
    }
}

