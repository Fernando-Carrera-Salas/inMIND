package com.kotlin.inmind.util

import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import com.kotlin.inmind.R
import com.kotlin.inmind.util.Constants.PREFERENCES
import com.kotlin.inmind.util.Constants.SLIDE_DIRECTION_DOWN
import com.kotlin.inmind.util.Constants.SLIDE_DIRECTION_LEFT
import com.kotlin.inmind.util.Constants.SLIDE_DIRECTION_RIGHT
import com.kotlin.inmind.util.Constants.SLIDE_DIRECTION_UP
import kotlin.math.abs

object Util
{
    fun loadColorOptions(context: Context): List<List<Color>>
    {
        val colorStrings = context.resources.getStringArray(R.array.inmind_game_colors)
        return colorStrings.map { colorString -> colorString.split("-").map { Color(android.graphics.Color.parseColor("#"+it.trim())) } }
    }

    fun changeActivitySlide(currentActivity: ComponentActivity, newActivity: Class<*>, slideDirection: Int = SLIDE_DIRECTION_RIGHT)
    {
        var enterAnim = R.anim.enter_from_right
        var exitAnim = R.anim.exit_to_left
        when (slideDirection)
        {
            SLIDE_DIRECTION_RIGHT -> {
                enterAnim = R.anim.enter_from_right
                exitAnim = R.anim.exit_to_left
            }
            SLIDE_DIRECTION_LEFT -> {
                enterAnim = R.anim.enter_from_left
                exitAnim = R.anim.exit_to_right
            }
            SLIDE_DIRECTION_UP -> {
                enterAnim = R.anim.enter_from_above
                exitAnim = R.anim.exit_to_below
            }
            SLIDE_DIRECTION_DOWN -> {
                enterAnim = R.anim.enter_from_below
                exitAnim = R.anim.exit_to_above
            }
        }
        val i = Intent(currentActivity, newActivity)
        currentActivity.startActivity(i)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        {
            currentActivity.overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim)
        }
        else
        {
            @Suppress("DEPRECATION")
            currentActivity.overridePendingTransition(enterAnim, exitAnim)
        }
        currentActivity.finish()
    }


    fun reduceSaturationToMinimum(color: Color, minSaturation: Float): Color
    {

        fun rgbToHsl(r: Float, g: Float, b: Float): FloatArray
        {
            val max = maxOf(r, g, b)
            val min = minOf(r, g, b)
            val delta = max - min

            val hue = when (max)
            {
                r -> 60 * (((g - b) / delta) % 6)
                g -> 60 * (((b - r) / delta) + 2)
                b -> 60 * (((r - g) / delta) + 4)
                else -> 0f
            }.let {
                if (it < 0) it + 360
                else it
            }

            val lightness = (max + min) / 2

            val saturation = if (delta == 0f) 0f else delta / (1 - abs(2 * lightness - 1))

            return floatArrayOf(hue, saturation, lightness)
        }


        fun hslToColor(hue: Float, saturation: Float, lightness: Float): Color
        {
            val c = (1 - abs(2 * lightness - 1)) * saturation
            val x = c * (1 - abs((hue / 60) % 2 - 1))
            val m = lightness - c / 2

            val (r, g, b) =
                when
                {
                    hue < 60 -> Triple(c, x, 0f)
                    hue < 120 -> Triple(x, c, 0f)
                    hue < 180 -> Triple(0f, c, x)
                    hue < 240 -> Triple(0f, x, c)
                    hue < 300 -> Triple(x, 0f, c)
                    else -> Triple(c, 0f, x)
                }

            return Color(red = r + m, green = g + m, blue = b + m)
        }


        val red = color.red
        val green = color.green
        val blue = color.blue

        val hsl = rgbToHsl(red, green, blue)

        hsl[1] = if (hsl[1] > minSaturation)
        {
            minSaturation + (hsl[1] - minSaturation) * 0.5f
        }
        else
        {
            hsl[1]
        }

        return hslToColor(hsl[0], hsl[1], hsl[2])
    }



    fun getFromPreferences(context: Context, key: String, default: Int): Int
    {
        return context.getSharedPreferences(PREFERENCES,MODE_PRIVATE).getInt(key,default)
    }

    fun getFromPreferences(context: Context, key: String, default: String): String
    {
        return context.getSharedPreferences(PREFERENCES,MODE_PRIVATE).getString(key,default)?: default
    }

    fun setToPreferences(context: Context, key: String, value: Int)
    {
        context.getSharedPreferences(PREFERENCES,MODE_PRIVATE).edit().putInt(key,value).apply()
    }

    fun setToPreferences(context: Context, key: String, value: String)
    {
        context.getSharedPreferences(PREFERENCES,MODE_PRIVATE).edit().putString(key,value).apply()
    }

}