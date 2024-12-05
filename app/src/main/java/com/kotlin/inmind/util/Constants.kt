package com.kotlin.inmind.util

object Constants
{
    const val PREFERENCES = "prefs"
    const val INMIND_COLORS = "inMIND_COLORS"
    const val INMIND_LEVEL = "inMIND_LEVEL"
    const val INMIND_PROGRESS = "inMIND_PROGRESS"

    val SHAPES = listOf("Triangle","Square","Circle","Star","Rhombus","Heart","Arch","Diamond","Moon")

    const val GAME_STATUS_NONE = 0
    const val GAME_STATUS_STARTING = 1
    const val GAME_STATUS_MEMORIZATION = 2
    const val GAME_STATUS_FADING = 3
    const val GAME_STATUS_PLAYING = 4
    const val GAME_STATUS_CHECKING = 5
    const val GAME_STATUS_VIEWING_PLAY = 6
    const val GAME_STATUS_VIEWING_MEMORY = 7


    const val SLIDE_DIRECTION_RIGHT = 1
    const val SLIDE_DIRECTION_LEFT = 2
    const val SLIDE_DIRECTION_UP = 3
    const val SLIDE_DIRECTION_DOWN = 4
}