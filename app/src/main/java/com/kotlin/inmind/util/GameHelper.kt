package com.kotlin.inmind.util

import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import com.kotlin.inmind.R
import com.kotlin.inmind.util.Constants.GAME_STATUS_PLAYING
import com.kotlin.inmind.util.Constants.GAME_STATUS_VIEWING_PLAY
import com.kotlin.inmind.util.Constants.SHAPES
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt

object GameHelper
{
    data class Game(
        val shapes: List<GameShape>,
        val gameConfig: GameConfig,
    )

    data class GameConfig(
        val boardSize: Int,
        val shapeSize: Int,
        val totalShapes: Int,
        val availableTypes: List<String>,
        val maxSameType: Int,
        val maxSameColor: Int,
        val memoryTime: Int,
        val gameTime: Int,
        val userLevel: Int,
        val userLevelCurrentProgress: Int,
        val userLevelMaxProgress: Int,
        val userLevelNextMaxProgress: Int,
    )


    data class GoalState(
        val icon: Int,
        val color: Color,
        val show: Boolean,
    )

    data class UserLevelAndProgressState(
        val level: Int,
        val progress: Int,
        val targetProgress: Int,
        val maxProgress: Int,
        val nextLevelMaxProgress: Int,
    )

    class GameShape(
        val id: Int,
        type:String,
        color: Color,
        gridSizePx: Int = 0,
        draggable: Boolean = true,
        topLeftSquareCol: Int = 0,
        topLeftSquareRow: Int = 0,
        x: Int = topLeftSquareRow*gridSizePx,
        y: Int = topLeftSquareCol*gridSizePx,
    )
    {
        var topLeftX by mutableIntStateOf(x)
        var topLeftY by mutableIntStateOf(y)
        var topLeftSquare by mutableStateOf(Pair(topLeftSquareCol,topLeftSquareRow))
        var alpha by mutableFloatStateOf(1f)
        var type by mutableStateOf(type)
        var color by mutableStateOf(color)
        var tapTimestamp by mutableLongStateOf(0L)

        var pressed by mutableStateOf(false)
        var removed by mutableStateOf(false)

        var isDraggable by mutableStateOf(draggable)
        var isDragging by mutableStateOf(false)
        var beingChecked by mutableStateOf(false)
    }



    fun maxProgressForLevel(
        level: Int
    ): Int {
        if (level == 1)
            return 800
        val multiplier = (level - 1) * 1.2
        return (multiplier * 1200).toInt()
    }

    fun calculateSidePadding(
        screenHeight: Int,
        screenWidth: Int,
        boardSize: Int,
        shapeSize: Int,
    ): Int
    {
        var sidePadding = 19
        val availableHeight = screenHeight - 300
        do
        {
            sidePadding += 1
            val estimatedBoardWidth = screenWidth - (sidePadding*2)
            val estimatedGridSize = estimatedBoardWidth / boardSize
            val estimatedShapeSize = estimatedGridSize * shapeSize
        }
        while (estimatedBoardWidth+estimatedShapeSize>availableHeight)

        return sidePadding
    }

    fun generateGameConfig(level: Int,
                           currentProgress: Int,
                           maxProgress: Int,
                           nextMaxProgress: Int,
    ): GameConfig
    {
        val boardSize: Int
        val shapeSize: Int
        val totalShapes: Int
        val shapeTypes: Int
        val maxType: Int
        val maxColor: Int
        val memoryTime: Int
        val gameTime: Int
        when (level)
        {
            1 -> {
                boardSize = (3..4).random()
                shapeSize = 1
                totalShapes = if (boardSize==3) 2 else 1
                shapeTypes = 3
                maxType = 1
                maxColor = 1
                memoryTime = 30
                gameTime = 60
            }
            2 -> {
                boardSize = (3..5).random()
                shapeSize = if (boardSize==5) 2 else 1
                totalShapes = if (boardSize==3) 2 else 1
                shapeTypes = 3
                maxType = 1
                maxColor = 1
                memoryTime = 30
                gameTime = 60
            }
            3 -> {
                boardSize = (4..6).random()
                shapeSize = if (boardSize==6) 2 else 1
                totalShapes = 2
                shapeTypes = 6
                maxType = 1
                maxColor = 1
                memoryTime = 30
                gameTime = 60
            }
            4 -> {
                boardSize = (5..7).random()
                shapeSize = 2
                totalShapes = 2
                shapeTypes = 6
                maxType = 1
                maxColor = 1
                memoryTime = 25
                gameTime = 55
            }
            5 -> {
                boardSize = (5..8).random()
                shapeSize = if (boardSize==8) 3 else 2
                totalShapes = if (boardSize==5) 3 else 2
                shapeTypes = 6
                maxType = 1
                maxColor = 1
                memoryTime = 25
                gameTime = 55
            }
            6 -> {
                boardSize = (5..9).random()
                shapeSize = if (boardSize==9) 3 else 2
                totalShapes = if (boardSize==5) 3 else 2
                shapeTypes = 6
                maxType = 1
                maxColor = 1
                memoryTime = 25
                gameTime = 55
            }
            7 -> {
                boardSize = (6..9).random()
                shapeSize = if (boardSize==9) 3 else 2
                totalShapes = if (boardSize==6) 3 else 2
                shapeTypes = 6
                maxType = 1
                maxColor = 1
                memoryTime = 25
                gameTime = 55
            }
            8 -> {
                boardSize = (6..9).random()
                shapeSize = 2
                totalShapes = 3
                shapeTypes = 6
                maxType = 1
                maxColor = 1
                memoryTime = 25
                gameTime = 55
            }
            9 -> {
                boardSize = (7..10).random()
                shapeSize = if (boardSize==10) 3 else 2
                totalShapes = 3
                shapeTypes = 6
                maxType = 1
                maxColor = 1
                memoryTime = 25
                gameTime = 50
            }
            10 -> {
                boardSize = (8..10).random()
                shapeSize = if (boardSize==10) 3 else 2
                totalShapes = 3
                shapeTypes = 6
                maxType = 1
                maxColor = 1
                memoryTime = 25
                gameTime = 50
            }
            else ->
            {
                return autoGenerateGameConfig(level,currentProgress, maxProgress, nextMaxProgress)
            }
        }

        return GameConfig(
            boardSize = boardSize,
            shapeSize = shapeSize,
            totalShapes = totalShapes,
            availableTypes = SHAPES.take(shapeTypes),
            maxSameType = maxType,
            maxSameColor = maxColor,
            memoryTime = memoryTime,
            gameTime = gameTime,

            userLevel = level,
            userLevelCurrentProgress = currentProgress,
            userLevelMaxProgress = maxProgress,
            userLevelNextMaxProgress = nextMaxProgress,
        )
    }

    private fun autoGenerateGameConfig(
        level: Int,
        currentProgress: Int,
        maxProgress: Int,
        nextMaxProgress: Int,
    ): GameConfig
    {
        val boardSize = (minOf(3 + level / 5, 20)..minOf(4 + level / 4, 20)).random()

        val maxShapeSize = boardSize / 2
        val minShapeSize = maxOf(1, boardSize / 10)
        val shapeSize = (minShapeSize..maxShapeSize).random()

        val totalShapes =
            when
            {
                level <= 80 -> 4 + (level - 10) / 10
                else -> minOf(10 + (level - 80) / 5, 36)
            }

        val shapeTypes =
            when
            {
                totalShapes <= 3 -> 3
                totalShapes <= 6 -> 6
                else -> 9
            }

        val maxRepetition =
            when
            {
                totalShapes <= shapeTypes -> 1
                totalShapes <= shapeTypes * 2 -> 2
                totalShapes <= shapeTypes * 3 -> 3
                else -> 4
            }

        val memoryTime = maxOf(3, 30 - (level / 5))
        val gameTime = maxOf(5, 60 - (level / 4))

        val maxPossibleShapes = (boardSize / shapeSize) * (boardSize / shapeSize)
        val adjustedTotalShapes = minOf(totalShapes, maxPossibleShapes)

        return GameConfig(
            boardSize = boardSize,
            shapeSize = shapeSize,
            totalShapes = adjustedTotalShapes,
            availableTypes = SHAPES.take(shapeTypes),
            maxSameType = maxRepetition,
            maxSameColor = maxRepetition,
            memoryTime = memoryTime,
            gameTime = gameTime,

            userLevel = level,
            userLevelCurrentProgress = currentProgress,
            userLevelMaxProgress = maxProgress,
            userLevelNextMaxProgress = nextMaxProgress,
        )
    }



    fun generateGame(
        gameConfig: GameConfig,
        availableColors: List<Color>,
    ): Game
    {
        val gameShapes = mutableListOf<GameShape>()
        var generatedGame = false
        val random = java.util.Random()

        while (!generatedGame)
        {
            var placementAttempts = 0
            val shapes = mutableListOf<GameShape>()
            var newId = 0
            repeat(gameConfig.totalShapes)
            {
                var type: String
                var sameType: Int
                do
                {
                    sameType = 1
                    type = gameConfig.availableTypes.random()
                    shapes.forEach{
                        if (it.type == type)
                            sameType++
                    }
                }
                while(sameType>gameConfig.maxSameType)


                var color: Color
                var sameColor: Int
                do
                {
                    sameColor = 1
                    color = availableColors.random()
                    shapes.forEach{
                        if (it.color == color)
                            sameColor++
                    }
                }
                while (sameColor>gameConfig.maxSameColor)

                newId++
                var topLeftSquare: Pair<Int, Int>
                var positionFound: Boolean

                do
                {
                    topLeftSquare = Pair(random.nextInt(gameConfig.boardSize),random.nextInt(gameConfig.boardSize))
                    positionFound = checkCollision(
                        targetSquare = topLeftSquare,
                        draggedId = newId,
                        shapes = shapes,
                        boardSize = gameConfig.boardSize,
                        shapeSize = gameConfig.shapeSize,
                        false,
                    )

                    if (!positionFound)
                    {
                        placementAttempts++
                        if (placementAttempts>100)
                            break
                    }
                }
                while (!positionFound)
                shapes.add(
                    GameShape(
                        id = newId,
                        type = type,
                        color = color,
                        draggable = false,
                        topLeftSquareCol = topLeftSquare.first,
                        topLeftSquareRow = topLeftSquare.second,
                    )
                )
            }

            if (placementAttempts<100)
                generatedGame = true

            if (generatedGame)
            {
                gameShapes.clear()
                gameShapes.addAll(shapes)
            }
        }

        return Game(
            shapes = gameShapes,
            gameConfig = gameConfig,
        )
    }


    fun checkCollision(
        targetSquare: Pair<Int, Int>,
        draggedId: Int,
        shapes: List<GameShape>,
        boardSize: Int,
        shapeSize: Int,
        dragging: Boolean,
    ): Boolean
    {
        if (boardSize-targetSquare.first<shapeSize)
        {
            return false
        }

        if (!dragging && boardSize-targetSquare.second<shapeSize)
        {
            return false
        }

        shapes.forEach {
            if (abs(it.topLeftSquare.first-targetSquare.first) <shapeSize&& abs(it.topLeftSquare.second-targetSquare.second)<shapeSize&&it.id!=draggedId)
            {
                return false
            }
        }

        return true
    }


    private fun calculateDistance(
        pos1: Pair<Int, Int>,
        pos2: Pair<Int, Int>,
    ): Double {
        val dx = pos1.first - pos2.first
        val dy = pos1.second - pos2.second
        return sqrt(dx * dx + dy * dy * 1.0)
    }

    private fun scoreShape(
        userShape: GameShape,
        originalShape: GameShape,
    ): Pair<Int, String>
    {
        var score = 0
        var goals = ""

        if (userShape.type == originalShape.type)
        {
            score += 10
            goals += "T"
        }

        if (userShape.color == originalShape.color)
        {
            score += 5
            goals +="C"
        }

        val distance = calculateDistance(userShape.topLeftSquare, originalShape.topLeftSquare)
        when
        {
            distance == 0.0 -> {
                score +=10
                goals += "E"
            }
            distance < 2.0 -> {
                score += 5
                goals += "N"
            }
        }

        if (score==25)
        {
            score = 100
        }

        if (score!=0)
        {
            score += (1..5).random()
        }

        return Pair(score,goals)
    }



    suspend fun checkGame(
        game: Game,
        shapes: List<GameShape>,
        selectedColors: List<Color>,
        minShapes: List<Int>,

        onChangeGoalShape: (GoalState) -> Unit,
        onChangeGoalColor: (GoalState) -> Unit,
        onChangeGoalLocation: (GoalState) -> Unit,
        onChangeGoalBonus: (GoalState) -> Unit,
        onChangeCurrentLevelState: (UserLevelAndProgressState) -> Unit,
        onChangeGameStatus: (Int) -> Unit,

        levelProgress: (progress: Int, level: Int) -> Unit,
    )
    {
        var targetLevelProgress: Int
        var currentLevelProgress = game.gameConfig.userLevelCurrentProgress
        var currentLevelMaxProgress = game.gameConfig.userLevelMaxProgress
        var nextLevelMaxProgress = game.gameConfig.userLevelNextMaxProgress
        var currentUserLevel = game.gameConfig.userLevel
        var allCorrect = true

        var goalShape: GoalState
        var goalColor: GoalState
        var goalLocation: GoalState
        var goalBonus: GoalState

        val matched = mutableSetOf<Int>()
        delay(500)

        if (shapes.size<game.shapes.size)
        {
            allCorrect = false
        }

        shapes.forEach { userShape ->
            userShape.beingChecked = true
            userShape.alpha = 1f
            delay(600)

            var bestMatch: Pair<Int, GameShape>? = null
            var closestDistanceTier = Int.MAX_VALUE

            for ((index, originalShape) in game.shapes.withIndex())
            {
                if (index in matched) continue

                val distance = calculateDistance(userShape.topLeftSquare, originalShape.topLeftSquare)
                val distanceTier =
                    when
                    {
                        distance <= 2.0 -> 1
                        distance <= 5.0 -> 2
                        distance <= 10.0 -> 3
                        else -> 4
                    }

                if (distanceTier < closestDistanceTier ||
                        (distanceTier == closestDistanceTier &&
                            bestMatch != null &&
                                ((userShape.type == originalShape.type &&
                                userShape.type != bestMatch.second.type)
                                    ||
                                (userShape.color == originalShape.color &&
                                userShape.color != bestMatch.second.color))
                        )
                )
                {
                    closestDistanceTier = distanceTier
                    bestMatch = index to originalShape
                }
            }

            userShape.alpha = 0.25f
            bestMatch!!.second.beingChecked = true
            bestMatch.second.alpha = 1f

            val addScore = scoreShape(userShape, bestMatch.second)
            matched.add(bestMatch.first)

            goalShape = GoalState(
                icon = minShapes[SHAPES.indexOf(bestMatch.second.type)],
                color =
                    if (addScore.second.contains('T'))
                    {
                        selectedColors[3]
                    }
                    else
                    {

                        selectedColors[0]
                    },
                show = true,
            )

            onChangeGoalShape(goalShape)

            delay(500)

            goalColor = GoalState(
                icon = R.drawable.palette,
                color =
                    if (addScore.second.contains('C'))
                    {
                        selectedColors[3]
                    }
                    else
                    {

                        selectedColors[0]
                    },
                show = true,
            )

            onChangeGoalColor(goalColor)

            delay(500)

            goalLocation = GoalState(
                icon = R.drawable.location,
                color =
                    when
                    {
                        addScore.second.contains("E") -> {
                            selectedColors[3]
                        }
                        addScore.second.contains('N') -> {
                            selectedColors[2]
                        }
                        else -> {
                            selectedColors[0]
                        }
                    },
                show = true,
            )

            onChangeGoalLocation(goalLocation)

            delay(500)

            targetLevelProgress = currentLevelProgress + addScore.first

            onChangeCurrentLevelState(
                UserLevelAndProgressState(
                    level = currentUserLevel,
                    progress = currentLevelProgress,
                    targetProgress = targetLevelProgress,
                    maxProgress = currentLevelMaxProgress,
                    nextLevelMaxProgress = nextLevelMaxProgress,
                )
            )

            delay(2000)

            if (currentLevelProgress+addScore.first>=currentLevelMaxProgress)
            {
                currentUserLevel++
                currentLevelProgress = currentLevelProgress + addScore.first - currentLevelMaxProgress
                currentLevelMaxProgress = nextLevelMaxProgress
                nextLevelMaxProgress = maxProgressForLevel(currentUserLevel)
                delay(1000)
            }
            else
            {
                currentLevelProgress += addScore.first
            }

            onChangeGoalShape(GoalState(goalShape.icon,goalShape.color,false))
            onChangeGoalColor(GoalState(goalColor.icon,goalColor.color,false))
            onChangeGoalLocation(GoalState(goalLocation.icon,goalLocation.color,false))

            levelProgress(
                currentLevelProgress,
                currentUserLevel
            )
            targetLevelProgress = 0

            onChangeCurrentLevelState(
                UserLevelAndProgressState(
                    level = currentUserLevel,
                    progress = currentLevelProgress,
                    targetProgress = targetLevelProgress,
                    maxProgress = currentLevelMaxProgress,
                    nextLevelMaxProgress = nextLevelMaxProgress,
                )
            )

            if (allCorrect&&addScore.second!="TCE")
                allCorrect = false

            delay(600)
            shapes.forEach {
                it.beingChecked = false
                it.alpha = 1f
            }
            game.shapes.forEach {
                it.beingChecked = false
            }
            delay(600)

        }
        if (allCorrect)
        {
            goalBonus = GoalState(
                icon = R.drawable.bonus,
                color = selectedColors[3],
                show = true,
            )

            onChangeGoalBonus(goalBonus)


            delay(500)

            val bonusScore = 200
            targetLevelProgress = currentLevelProgress + bonusScore

            onChangeCurrentLevelState(
                UserLevelAndProgressState(
                    level = currentUserLevel,
                    progress = currentLevelProgress,
                    targetProgress = targetLevelProgress,
                    maxProgress = currentLevelMaxProgress,
                    nextLevelMaxProgress = nextLevelMaxProgress,
                )
            )

            delay(2000)
            if (currentLevelProgress+bonusScore>currentLevelMaxProgress)
            {
                currentUserLevel++
                currentLevelProgress = currentLevelProgress + bonusScore - currentLevelMaxProgress
                currentLevelMaxProgress = nextLevelMaxProgress
                nextLevelMaxProgress = maxProgressForLevel(currentUserLevel)
                delay(1000)
            }
            else
            {
                currentLevelProgress += bonusScore
            }
            levelProgress(
                currentLevelProgress,
                currentUserLevel
            )
            targetLevelProgress = 0

            onChangeCurrentLevelState(
                UserLevelAndProgressState(
                    level = currentUserLevel,
                    progress = currentLevelProgress,
                    targetProgress = targetLevelProgress,
                    maxProgress = currentLevelMaxProgress,
                    nextLevelMaxProgress = nextLevelMaxProgress,
                )
            )
        }
        delay(500)

        goalBonus = GoalState(
            icon = R.drawable.bonus,
            color = selectedColors[3],
            show = false,
        )
        onChangeGoalBonus(goalBonus)

        onChangeGameStatus(GAME_STATUS_VIEWING_PLAY)
    }




    object GameShapeInteractions
    {
        fun onDragStart(
            offset: Offset,
            gameStatus: Int,
            createShape: GameShape,
            gridSizePx: Int,
            shapeSize: Int,
            boardSize: Int,
            shapes: MutableList<GameShape>,
            showBottomMenu: Boolean,
            maxId: Int,

            onChangeMaxId: (Int) -> Unit,
            onChangeDraggedShape: (GameShape) -> Unit,
        )
        {
            if (gameStatus == GAME_STATUS_PLAYING)
            {
                if (offset.x > createShape.topLeftX &&
                    offset.x < createShape.topLeftX + gridSizePx * shapeSize &&
                    offset.y > createShape.topLeftY &&
                    offset.y < createShape.topLeftY + gridSizePx * shapeSize &&
                    showBottomMenu
                )
                {
                    onChangeMaxId(maxId+1)
                    val newShape =
                        GameShape(
                            id = maxId,
                            gridSizePx = gridSizePx,
                            draggable = true,
                            topLeftSquareCol = boardSize / 2,
                            topLeftSquareRow = boardSize,
                            type = createShape.type,
                            color = createShape.color,
                        )
                    shapes.add(newShape)
                    onChangeDraggedShape(newShape)
                    createShape.alpha = 0f
                }
                else
                {
                    shapes.forEach {
                        if (offset.x > it.topLeftX &&
                            offset.x < it.topLeftX + gridSizePx * shapeSize &&
                            offset.y > it.topLeftY &&
                            offset.y < it.topLeftY + gridSizePx * shapeSize &&
                            it.isDraggable
                        )
                        {
                            onChangeDraggedShape(it)
                        }
                    }
                }
            }
        }

        fun onDrag(
            change: PointerInputChange,
            gameStatus: Int,
            draggedShape: GameShape?,
            boardSize: Int,
            shapeSize: Int,
            gridSizePx: Int,
            gridPoints: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>,
            shapes: List<GameShape>,
        )
        {
            if (gameStatus == GAME_STATUS_PLAYING && draggedShape != null)
            {
                val desiredX = change.position.x - (shapeSize / 2) * gridSizePx
                val desiredY = change.position.y - (shapeSize / 2) * gridSizePx
                var closestDistance: Double? = null
                var closestPoint = Pair(Pair(0, 0), Pair(0, 0))
                val checkGrid = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
                checkGrid.addAll(gridPoints)
                if (shapeSize == 1)
                {
                    for (x in 0..<boardSize)
                    {
                        checkGrid.add(
                            Pair(
                                Pair(x, boardSize),
                                Pair(x * gridSizePx, boardSize * gridSizePx)
                            )
                        )
                    }
                }
                checkGrid.forEach {
                    val d = hypot(
                        desiredX - it.second.first.toDouble(),
                        desiredY - it.second.second.toDouble()
                    )
                    if (closestDistance == null || closestDistance!! > d)
                    {
                        closestDistance = d
                        closestPoint = it
                    }
                }

                if (checkCollision(
                        closestPoint.first,
                        draggedShape.id,
                        shapes,
                        boardSize,
                        shapeSize,
                        true,
                    )
                )
                {
                    draggedShape.topLeftX = closestPoint.second.first
                    draggedShape.topLeftY = closestPoint.second.second
                    draggedShape.topLeftSquare = closestPoint.first
                    if (draggedShape.topLeftSquare.second > boardSize - shapeSize)
                    {
                        draggedShape.alpha = 0.25f
                    }
                    else
                    {
                        draggedShape.alpha = 1f
                    }
                }
            }
            change.consume()
        }

        fun onDragEnd(
            gameStatus: Int,
            draggedShape: GameShape?,
            createShape: GameShape,
            boardSize: Int,
            shapeSize: Int,
            shapes: List<GameShape>,
            game: Game,
            onChangeShowBottomMenu: (Boolean) -> Unit,
            onChangeDraggedShape: (GameShape?) -> Unit,
        )
        {
            if (gameStatus == GAME_STATUS_PLAYING && draggedShape != null)
            {
                draggedShape.isDragging = false
                createShape.alpha = 1f

                if (draggedShape.topLeftSquare.second > boardSize - shapeSize)
                {
                    draggedShape.removed = true
                }
                else
                {
                    if (shapes.size >= game.gameConfig.totalShapes)
                    {
                        onChangeShowBottomMenu(false)
                    }
                }
            }
            onChangeDraggedShape(null)
        }

        fun onTap(
            offset: Offset,
            gameStatus: Int,
            createShape: GameShape,
            gridSizePx: Int,
            shapeSize: Int,
            showBottomMenu: Boolean,
            gridPoints: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>,
            shapes: MutableList<GameShape>,
            game: Game,
            maxId: Int,

            onChangeMaxId: (Int) -> Unit,
            onChangeShowBottomMenu: (Boolean) -> Unit,
        )
        {
            if (gameStatus == GAME_STATUS_PLAYING)
            {
                if (offset.x > createShape.topLeftX &&
                    offset.x < createShape.topLeftX + gridSizePx * shapeSize &&
                    offset.y > createShape.topLeftY &&
                    offset.y < createShape.topLeftY + gridSizePx * shapeSize &&
                    showBottomMenu
                )
                {
                    var emptySpace = false
                    gridPoints.forEach {
                        if (checkCollision(
                                it.first,
                                -2,
                                shapes,
                                game.gameConfig.boardSize,
                                game.gameConfig.shapeSize,
                                false,
                            ) && !emptySpace
                        ) {
                            emptySpace = true
                            onChangeMaxId(maxId+1)
                            val newShape =
                                GameShape(
                                    id = maxId,
                                    gridSizePx = gridSizePx,
                                    draggable = true,
                                    topLeftSquareCol = it.first.first,
                                    topLeftSquareRow = it.first.second,
                                    type = createShape.type,
                                    color = createShape.color,
                                    x = it.second.first,
                                    y = it.second.second,
                                )
                            shapes.add(newShape)
                            if (shapes.size >= game.gameConfig.totalShapes)
                            {
                                onChangeShowBottomMenu(false)
                            }
                        }
                    }
                } else {
                    shapes.forEach {
                        if (offset.x > it.topLeftX &&
                            offset.x < it.topLeftX + gridSizePx * shapeSize &&
                            offset.y > it.topLeftY &&
                            offset.y < it.topLeftY + gridSizePx * shapeSize &&
                            it.isDraggable
                        )
                        {
                            if (System.currentTimeMillis() - it.tapTimestamp < 500)
                            {
                                it.removed = true
                            }
                            else
                            {
                                it.tapTimestamp = System.currentTimeMillis()
                            }
                        }
                    }
                }
            }
        }

        suspend fun PressGestureScope.onPress(
            offset: Offset,
            gameStatus: Int,
            gridSizePx: Int,
            shapeSize: Int,
            showBottomMenu: Boolean,
            createShape: GameShape,
            shapes: List<GameShape>,
        )
        {
            if (gameStatus == GAME_STATUS_PLAYING)
            {
                if (offset.x > createShape.topLeftX &&
                    offset.x < createShape.topLeftX + gridSizePx * shapeSize &&
                    offset.y > createShape.topLeftY &&
                    offset.y < createShape.topLeftY + gridSizePx * shapeSize &&
                    showBottomMenu
                )
                {
                    createShape.pressed = true
                    try
                    {
                        awaitRelease()
                        createShape.pressed = false
                    }
                    catch (e: CancellationException)
                    {
                        createShape.pressed = false
                    }
                }
                else
                {
                    shapes.forEach {
                        if (offset.x > it.topLeftX &&
                            offset.x < it.topLeftX + gridSizePx * shapeSize &&
                            offset.y > it.topLeftY &&
                            offset.y < it.topLeftY + gridSizePx * shapeSize &&
                            it.isDraggable
                        )
                        {
                            it.pressed = true
                            try
                            {
                                awaitRelease()
                                it.pressed = false
                            }
                            catch (e: CancellationException)
                            {
                                it.pressed = false
                            }
                        }
                    }
                }
            }
        }
    }
}