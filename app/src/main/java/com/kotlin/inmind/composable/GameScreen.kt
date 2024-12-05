package com.kotlin.inmind.composable

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.kotlin.inmind.R
import com.kotlin.inmind.composable.Common.AnimatedGridBackground
import com.kotlin.inmind.composable.Common.AnimatedImage
import com.kotlin.inmind.composable.Common.InMindImageButton
import com.kotlin.inmind.composable.Common.InMindSymbolImageButton
import com.kotlin.inmind.composable.Common.ScreenMeasurements
import com.kotlin.inmind.composable.Common.rememberScreenMeasurements
import com.kotlin.inmind.util.Constants.GAME_STATUS_CHECKING
import com.kotlin.inmind.util.Constants.GAME_STATUS_FADING
import com.kotlin.inmind.util.Constants.GAME_STATUS_MEMORIZATION
import com.kotlin.inmind.util.Constants.GAME_STATUS_NONE
import com.kotlin.inmind.util.Constants.GAME_STATUS_PLAYING
import com.kotlin.inmind.util.Constants.GAME_STATUS_STARTING
import com.kotlin.inmind.util.Constants.GAME_STATUS_VIEWING_MEMORY
import com.kotlin.inmind.util.Constants.GAME_STATUS_VIEWING_PLAY
import com.kotlin.inmind.util.Constants.SHAPES
import com.kotlin.inmind.util.GameHelper.Game
import com.kotlin.inmind.util.GameHelper.GameShape
import com.kotlin.inmind.util.GameHelper.GameShapeInteractions.onDrag
import com.kotlin.inmind.util.GameHelper.GameShapeInteractions.onDragEnd
import com.kotlin.inmind.util.GameHelper.GameShapeInteractions.onDragStart
import com.kotlin.inmind.util.GameHelper.GameShapeInteractions.onPress
import com.kotlin.inmind.util.GameHelper.GameShapeInteractions.onTap
import com.kotlin.inmind.util.GameHelper.GoalState
import com.kotlin.inmind.util.GameHelper.UserLevelAndProgressState
import com.kotlin.inmind.util.GameHelper.calculateSidePadding
import com.kotlin.inmind.util.GameHelper.checkGame
import kotlinx.coroutines.delay

object GameScreen
{

    @Composable
    fun GameScreenComposable(onBack:() -> Unit, selectedColors: List<Color>, game: Game, nextGame:() -> Unit, background: Color, levelProgress:(progress:Int, level: Int) -> Unit)
    {
        val screenMeasurements = rememberScreenMeasurements()
        var showBackDialog by remember { mutableStateOf(false) }

        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = background),
        )
        {
            AnimatedGridBackground(false)

            GameBoardComposable(
                screenMeasurements = screenMeasurements,
                onBack = {
                    showBackDialog = true
                },
                selectedColors = selectedColors,
                game = game,
                nextGame = nextGame,
                levelProgress = levelProgress,
            )

            if (showBackDialog)
            {
                BackDialogComposable(
                    onChangeShowBackDialog = { value ->
                        showBackDialog = value
                    },
                    onBack = onBack,
                )
            }
        }


        BackHandler {
            showBackDialog = !showBackDialog
        }
    }

    @Composable
    fun BackDialogComposable(
        onChangeShowBackDialog: (Boolean) -> Unit,
        onBack: () -> Unit,
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(android.graphics.Color.parseColor("#66FFFFFF")))
                .pointerInput(Unit) { detectTapGestures {} },
                contentAlignment = Alignment.Center,
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(android.graphics.Color.parseColor("#66000000")))
                    .padding(20.dp),
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(1f),
                    )
                    {
                        InMindImageButton(
                            imageResource = R.drawable.exit,
                            isPressable = false,
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(40.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    )
                    {
                        InMindImageButton(
                            imageResource = R.drawable.cancel,
                            size = 60,
                            pressedColor = colorResource(R.color.im_red),
                            onClick = {
                                onChangeShowBackDialog(false)
                            },
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        InMindImageButton(
                            imageResource = R.drawable.accept,
                            size = 60,
                            pressedColor = colorResource(R.color.im_green),
                            onClick = onBack,
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun GameBoardComposable(
        screenMeasurements: ScreenMeasurements,
        selectedColors: List<Color>,
        game: Game,
        nextGame:() -> Unit,
        onBack:() -> Unit,
        levelProgress: (progress:Int, level:Int) -> Unit
    )
    {
        val mBoardSize by remember { mutableIntStateOf(game.gameConfig.boardSize) }
        val mShapeSize by remember { mutableIntStateOf(game.gameConfig.shapeSize) }

        val sidePadding by remember { mutableIntStateOf(calculateSidePadding(screenMeasurements.heightDp,screenMeasurements.widthDp,mBoardSize,mShapeSize)) }

        val boardWidthDp = screenMeasurements.widthDp - (sidePadding*2)
        val boardWidthPx = screenMeasurements.widthPx - with(LocalDensity.current) { (sidePadding*2).dp.toPx() }.toInt()

        val gridSizeDp by remember { mutableIntStateOf(boardWidthDp / mBoardSize) }
        val gridSizePx by remember { mutableIntStateOf(boardWidthPx / mBoardSize) }

        var currentLevelState by remember {
            mutableStateOf(
                UserLevelAndProgressState(
                    level = game.gameConfig.userLevel,
                    progress = game.gameConfig.userLevelCurrentProgress,
                    targetProgress = 0,
                    maxProgress = game.gameConfig.userLevelMaxProgress,
                    nextLevelMaxProgress = game.gameConfig.userLevelNextMaxProgress,
                )
            )
        }

        var goalShape by remember { mutableStateOf(GoalState(R.drawable.min_shape_triangle,selectedColors[0],false))}
        var goalColor by remember { mutableStateOf(GoalState(R.drawable.palette,selectedColors[0],false))}
        var goalLocation by remember { mutableStateOf(GoalState(R.drawable.location,selectedColors[0],false))}
        var goalBonus by remember { mutableStateOf(GoalState(R.drawable.bonus,selectedColors[3],false))}

        val gridPoints = mutableListOf<Pair<Pair<Int,Int>,Pair<Int,Int>>>()

        val ibShapes = listOf(
            ImageBitmap.imageResource(R.drawable.shape_triangle),
            ImageBitmap.imageResource(R.drawable.shape_square),
            ImageBitmap.imageResource(R.drawable.shape_circle),
            ImageBitmap.imageResource(R.drawable.shape_star),
            ImageBitmap.imageResource(R.drawable.shape_rhombus),
            ImageBitmap.imageResource(R.drawable.shape_heart),
            ImageBitmap.imageResource(R.drawable.shape_arch),
            ImageBitmap.imageResource(R.drawable.shape_diamond),
            ImageBitmap.imageResource(R.drawable.shape_moon),
        )

        val minShapes = listOf(
            R.drawable.min_shape_triangle,
            R.drawable.min_shape_square,
            R.drawable.min_shape_circle,
            R.drawable.min_shape_star,
            R.drawable.min_shape_rhombus,
            R.drawable.min_shape_heart,
            R.drawable.min_shape_arch,
            R.drawable.min_shape_diamond,
            R.drawable.min_shape_moon,
        )

        val gridBackgrounds = listOf(
            R.drawable.bg0,
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3,
            R.drawable.bg4,
            R.drawable.bg5,
            R.drawable.bg6,
            R.drawable.bg7,
            R.drawable.bg8,
            R.drawable.bg9,
            R.drawable.bg10,
            R.drawable.bg11,
            R.drawable.bg12,
            R.drawable.bg13,
            R.drawable.bg14,
            R.drawable.bg15,
            R.drawable.bg16,
            R.drawable.bg17,
            R.drawable.bg18,
            R.drawable.bg19,
            R.drawable.bg20,
            R.drawable.bg21,
        )

        val mBackground by remember { mutableIntStateOf(gridBackgrounds.random()) }

        val ibBackground = ImageBitmap.imageResource(mBackground)

        var memorySeconds by remember { mutableIntStateOf(game.gameConfig.memoryTime) }
        var gameSeconds by remember { mutableIntStateOf(game.gameConfig.gameTime) }


        for (row in 0 until mBoardSize)
        {
            for (col in 0 until mBoardSize)
            {
                gridPoints.add(Pair(Pair(row,col),Pair(row * gridSizePx, col * gridSizePx)))
            }
        }

        val margin = (with(LocalDensity.current) { 70.dp.toPx() }.toInt())

        var extraMargin = 0
        if (gridSizeDp*mShapeSize < 40)
        {
            extraMargin = (with(LocalDensity.current) { 40.dp.toPx() }.toInt() - gridSizePx*mShapeSize)
        }

        val createShape by remember {
            mutableStateOf(
                GameShape(
                    id = -1,
                    color = selectedColors.random(),
                    type = game.gameConfig.availableTypes.random(),
                    gridSizePx = gridSizePx,
                    draggable = false,
                    topLeftSquareRow = 0,
                    topLeftSquareCol = 0,
                    x = (boardWidthPx/2-gridSizePx*mShapeSize/2f).toInt(),
                    y = gridSizePx * mBoardSize + margin + extraMargin,
                )
            )
        }

        val shapes = remember { mutableStateListOf<GameShape>() }

        var gameStatus by remember { mutableIntStateOf(GAME_STATUS_NONE) }
        var showBottomMenu by remember { mutableStateOf(false) }

        var triggerCheckGame by remember { mutableStateOf(false) }

        LaunchedEffect(triggerCheckGame)
        {
            if (triggerCheckGame)
            {
                checkGame(
                    game = game,
                    shapes = shapes,
                    selectedColors = selectedColors,
                    minShapes = minShapes,
                    onChangeGoalShape = {value ->
                        goalShape = value
                    },
                    onChangeGoalColor = {value ->
                        goalColor = value
                    },
                    onChangeGoalLocation = {value ->
                        goalLocation = value
                    },
                    onChangeGoalBonus = {value ->
                        goalBonus = value
                    },
                    onChangeGameStatus = {value ->
                        gameStatus = value
                    },
                    onChangeCurrentLevelState = {value ->
                        currentLevelState = value
                    },
                    levelProgress = levelProgress,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
        )
        {
            TopBarComposable(
                game = game,
                gameStatus = gameStatus,
                memorySeconds = memorySeconds,
                gameSeconds = gameSeconds,
                onChangeGameStatus = {value ->
                    gameStatus = value
                },
                onChangeMemorySeconds = {value ->
                    memorySeconds = value
                },
                onChangeGameSeconds = {value ->
                    gameSeconds = value
                },
                onChangeShowBottomMenu = {value ->
                    showBottomMenu = value
                },
                onBack = onBack,
                nextGame = nextGame,

                checkGame = {
                    triggerCheckGame = true
                },
            )
            Box()
            {
                BoardBackgroundComposable(
                    boardSize = mBoardSize,
                    gridSizeDp = gridSizeDp,
                    gridSizePx = gridSizePx,
                    sidePadding = sidePadding,
                    ibBackground = ibBackground,
                    gridPoints = gridPoints,
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible =
                        gameStatus == GAME_STATUS_MEMORIZATION ||
                        gameStatus == GAME_STATUS_CHECKING ||
                        gameStatus == GAME_STATUS_VIEWING_PLAY ||
                        gameStatus == GAME_STATUS_VIEWING_MEMORY,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 500))
                )
                {
                    MemoryShapesComposable(
                        game = game,
                        sidePadding = sidePadding,
                        gameStatus = gameStatus,
                        gridSizePx = gridSizePx,
                        ibShapes = ibShapes,
                        shapeSize = mShapeSize,
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible =
                        gameStatus == GAME_STATUS_PLAYING ||
                        gameStatus == GAME_STATUS_CHECKING ||
                        gameStatus == GAME_STATUS_VIEWING_PLAY ||
                        gameStatus == GAME_STATUS_VIEWING_MEMORY,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 500))
                )
                {
                    GameShapesComposable(
                        game = game,
                        sidePadding = sidePadding,
                        gameStatus = gameStatus,
                        createShape = createShape,
                        showBottomMenu = showBottomMenu,
                        gridSizePx = gridSizePx,
                        boardSize = mBoardSize,
                        shapeSize = mShapeSize,
                        shapes = shapes,
                        gridPoints = gridPoints,
                        ibShapes = ibShapes,
                        onChangeShowBottomMenu = {value ->
                            showBottomMenu = value
                        }
                    )
                }

                if (gameStatus==GAME_STATUS_PLAYING && showBottomMenu)
                {
                    BottomControlsComposable(
                        game = game,
                        createShape = createShape,
                        sidePadding = sidePadding,
                        gridSizeDp = gridSizeDp,
                        gridSizePx = gridSizePx,
                        boardSize = mBoardSize,
                        shapeSize = mShapeSize,
                        ibShapes = ibShapes,
                        minShapes = minShapes,
                        selectedColors = selectedColors,
                    )
                }

                if (gameStatus == GAME_STATUS_CHECKING || gameStatus == GAME_STATUS_VIEWING_PLAY || gameStatus == GAME_STATUS_VIEWING_MEMORY)
                {
                    GameCheckingComposable(
                        sidePadding = sidePadding,
                        boardWidthDp = boardWidthDp,
                        currentLevelState = currentLevelState,
                        selectedColors = selectedColors,
                        gameStatus = gameStatus,
                        goalShape = goalShape,
                        goalColor = goalColor,
                        goalLocation = goalLocation,
                        goalBonus = goalBonus,
                    )
                }
            }
        }

    }



    @Composable
    fun GameTimer(
        seconds: Int,
        max: Int,
        onTimeEnd: () -> Unit,
    )
    {
        var remainingSeconds by remember(seconds) { mutableIntStateOf(seconds) }
        val progress by animateFloatAsState(
            targetValue = remainingSeconds.toFloat() / max,
            animationSpec = tween(durationMillis = 100),
            label = "PlacementTimerAnimation",
        )

        LaunchedEffect(remainingSeconds)
        {
            if (remainingSeconds>-1)
            {
                delay(1000L)
                remainingSeconds -= 1
            }
            else
            {
                onTimeEnd()
            }
        }

        if (remainingSeconds>-1)
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp),
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scaleX = -1f, scaleY = 1f),
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        color = Color.LightGray,
                        trackColor = Color.Transparent,
                        strokeWidth = 9.dp,
                        modifier = Modifier.fillMaxSize(),
                    )
                    CircularProgressIndicator(
                        progress = { progress },
                        color = Color.White,
                        trackColor = Color.LightGray,
                        strokeWidth = 5.dp,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                    )
                }
                Text(
                    text = remainingSeconds.toString(),
                    fontSize = 40.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.merge(
                        TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(1f, 1f),
                                blurRadius = 5f,
                            ),
                        )),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }


    @Composable
    fun MemoryTimer(
        seconds: Int,
        onTimeEnd: () -> Unit = {},
    )
    {
        var remainingSeconds by remember(seconds) { mutableIntStateOf(seconds) }
        val transition = remember { Animatable(3f) }

        LaunchedEffect(remainingSeconds) {
            if (remainingSeconds>-1)
            {
                transition.snapTo(3f)
                transition.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
                )
                remainingSeconds--
            }
            else
            {
                onTimeEnd()
            }
        }

        if (remainingSeconds>-1)
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp),
            )
            {
                Text(
                    text = remainingSeconds.toString(),
                    fontSize = 40.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.merge(
                        TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(1f, 1f),
                                blurRadius = 5f
                            ),
                        )),
                    modifier = Modifier
                        .scale(transition.value)
                        .alpha(transition.value * -1 + 3)
                        .fillMaxWidth(),
                )
            }
        }
    }



    @Composable
    fun UserLevelAndProgress(
        currentLevelState: UserLevelAndProgressState,
        selectedColors: List<Color>,
    )
    {
        val yellowProgress = remember { Animatable(currentLevelState.progress / currentLevelState.maxProgress.toFloat()) }
        val greenProgress = remember { Animatable(currentLevelState.progress / currentLevelState.maxProgress.toFloat()) }
        var level by remember { mutableIntStateOf(currentLevelState.level) }

        LaunchedEffect(currentLevelState)
        {
            if (currentLevelState.targetProgress>0)
            {
                val levelProgress = currentLevelState.maxProgress - currentLevelState.progress
                val progressToAdd = currentLevelState.targetProgress - currentLevelState.progress

                if (currentLevelState.targetProgress < currentLevelState.maxProgress)
                {
                    val progressToNextLevel = (currentLevelState.progress + progressToAdd) / currentLevelState.maxProgress.toFloat()

                    yellowProgress.animateTo(
                        progressToNextLevel,
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    )
                    delay(200)
                    greenProgress.animateTo(
                        progressToNextLevel,
                        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    )

                }
                else
                {
                    yellowProgress.animateTo(
                        1f,
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    )
                    delay(200)
                    greenProgress.animateTo(
                        1f,
                        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                    )

                    level++
                    yellowProgress.snapTo(0f)
                    greenProgress.snapTo(0f)

                    val progressToNextLevel = (progressToAdd - levelProgress) / currentLevelState.nextLevelMaxProgress.toFloat()
                    yellowProgress.animateTo(
                        progressToNextLevel,
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    )
                    delay(200)
                    greenProgress.animateTo(
                        progressToNextLevel,
                        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
        )
        {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .alpha(0.5f)
                    .fillMaxWidth(),
            )
            {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black, CircleShape),
                )
                Box(
                    modifier = Modifier
                        .padding(25.dp, 15.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black)
                        .height(20.dp),
                )
            }
            Box(
                modifier = Modifier
                    .padding(44.dp, 20.dp, 5.dp, 0.dp)
                    .fillMaxWidth(yellowProgress.value)
                    .clip(RoundedCornerShape(0.dp, 5.dp, 5.dp, 0.dp))
                    .background(selectedColors[2])
                    .height(10.dp),
            )
            Box(
                modifier = Modifier
                    .padding(44.dp, 20.dp, 5.dp, 0.dp)
                    .fillMaxWidth(greenProgress.value)
                    .clip(RoundedCornerShape(0.dp, 5.dp, 5.dp, 0.dp))
                    .background(selectedColors[3])
                    .height(10.dp),
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
                    .background(selectedColors[3], CircleShape),
                contentAlignment = Alignment.Center,
            )
            {
                Text(
                    text = level.toString(),
                    fontSize = 22.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.merge(
                        TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(1f, 1f),
                                blurRadius = 5f,
                            )
                        ),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }


    @Composable
    fun TopBarComposable(
        game: Game,
        gameStatus: Int,
        memorySeconds: Int,
        gameSeconds: Int,

        onChangeGameStatus: (Int) -> Unit,
        onChangeShowBottomMenu: (Boolean) -> Unit,
        onChangeMemorySeconds: (Int) -> Unit,
        onChangeGameSeconds: (Int) -> Unit,
        onBack: () -> Unit,

        checkGame: () -> Unit,
        nextGame: () -> Unit,
    )
    {
        val eyeFrames = listOf(
            ImageBitmap.imageResource(R.drawable.eye_0),
            ImageBitmap.imageResource(R.drawable.eye_1),
            ImageBitmap.imageResource(R.drawable.eye_2),
            ImageBitmap.imageResource(R.drawable.eye_3),
            ImageBitmap.imageResource(R.drawable.eye_4),
            ImageBitmap.imageResource(R.drawable.eye_5),
            ImageBitmap.imageResource(R.drawable.eye_6),
        )
        val ibQuestion = ImageBitmap.imageResource(R.drawable.question)

        Box(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(0.dp, 20.dp)
                    .height(100.dp)
                    .alpha(0.5f),
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .align(Alignment.Center)
                        .height(70.dp),
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                        .background(Color.White),
                )
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                        .background(Color(android.graphics.Color.parseColor("#66000000"))),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center,
                )
                {
                    InMindImageButton(
                        imageResource = R.drawable.back_arrow,
                        size = 50,
                        pressedColor = colorResource(R.color.im_red),
                        onClick = onBack,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                )

                Box(
                    modifier = Modifier
                        .size(100.dp,100.dp),
                )
                {
                    when (gameStatus)
                    {
                        GAME_STATUS_NONE ->
                        {
                            LaunchedEffect(Unit)
                            {
                                delay(2000)
                                onChangeGameStatus(GAME_STATUS_STARTING)
                            }
                            AnimatedImage(
                                frames = eyeFrames,
                                animationSpeed = 50,
                                repeatMode = true,
                                minWaitTime = 500,
                                maxWaitTime = 3000,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        GAME_STATUS_STARTING ->
                        {
                            LaunchedEffect(Unit)
                            {
                                delay(1000)
                                onChangeGameStatus(GAME_STATUS_MEMORIZATION)
                            }
                            AnimatedImage(
                                frames = eyeFrames,
                                animationSpeed = 150,
                                repeatMode = false,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        GAME_STATUS_MEMORIZATION ->
                        {
                            MemoryTimer(
                                seconds = memorySeconds,
                                onTimeEnd = {
                                    onChangeGameStatus(GAME_STATUS_FADING)
                                    onChangeMemorySeconds(-1)
                                    onChangeShowBottomMenu(true)
                                },
                            )
                        }
                        GAME_STATUS_FADING ->
                        {
                            LaunchedEffect(Unit)
                            {
                                delay(1000)
                                onChangeGameStatus(GAME_STATUS_PLAYING)
                            }
                            AnimatedImage(
                                frames = eyeFrames,
                                animationSpeed = 150,
                                repeatMode = false,
                                reverse = true,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        GAME_STATUS_PLAYING ->
                        {
                            GameTimer(
                                seconds = gameSeconds,
                                max = game.gameConfig.gameTime,
                                onTimeEnd = {
                                    onChangeGameSeconds(-1)
                                    onChangeGameStatus(GAME_STATUS_CHECKING)
                                },
                            )
                        }
                        GAME_STATUS_CHECKING ->
                        {
                            Image(
                                bitmap = ibQuestion,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            )
                            checkGame()
                        }
                        GAME_STATUS_VIEWING_PLAY ->
                        {
                            AnimatedImage(
                                frames = eyeFrames,
                                animationSpeed = 50,
                                repeatMode = true,
                                minWaitTime = 500,
                                maxWaitTime = 3000,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        onChangeGameStatus(GAME_STATUS_VIEWING_MEMORY)
                                    },
                            )
                        }
                        GAME_STATUS_VIEWING_MEMORY ->
                        {
                            AnimatedImage(
                                frames = eyeFrames,
                                animationSpeed = 50,
                                repeatMode = false,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        onChangeGameStatus(GAME_STATUS_VIEWING_PLAY)
                                    },
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                )
                {
                    if (gameStatus==GAME_STATUS_MEMORIZATION||gameStatus==GAME_STATUS_PLAYING)
                    {
                        InMindImageButton(
                            imageResource = R.drawable.skip_arrow,
                            size = 50,
                            pressedColor = colorResource(R.color.im_blue),
                            onClick = {
                                when (gameStatus)
                                {
                                    GAME_STATUS_MEMORIZATION -> {
                                        if (memorySeconds>0)
                                        {
                                            onChangeMemorySeconds(-1)
                                        }
                                    }
                                    GAME_STATUS_PLAYING -> {
                                        if (gameSeconds>0)
                                        {
                                            onChangeGameSeconds(-1)
                                        }
                                    }
                                }
                            },
                        )
                    }
                    if (gameStatus>GAME_STATUS_CHECKING)
                    {
                        InMindImageButton(
                            imageResource = R.drawable.play,
                            size = 50,
                            pressedColor = colorResource(R.color.im_green),
                            onClick = {
                                nextGame()
                            },
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun BoardBackgroundComposable(
        boardSize: Int,
        gridSizeDp: Int,
        gridSizePx: Int,
        sidePadding: Int,
        ibBackground: ImageBitmap,
        gridPoints: List<Pair<Pair<Int,Int>,Pair<Int,Int>>>,
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(gridSizeDp.dp * boardSize)
                .padding(sidePadding.dp, 0.dp, sidePadding.dp, 0.dp)
                .clip(shape = RoundedCornerShape(20.dp)),
        )
        {
            Canvas(
                modifier = Modifier
                    .fillMaxSize(),
            )
            {
                drawRect(
                    color = Color(android.graphics.Color.parseColor("#E0E0E0")),
                    topLeft = Offset(0f, 0f),
                    size = Size(gridSizePx*boardSize.toFloat(), gridSizePx*boardSize.toFloat()),
                )

                drawImage(
                    image = ibBackground,
                    alpha = 1f,
                    dstSize = IntSize(gridSizePx*boardSize,gridSizePx*boardSize),
                    dstOffset = IntOffset(0,0),
                    colorFilter = ColorFilter.tint(Color.Black),
                )

                gridPoints.forEach{
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(it.second.first.toFloat()+1, it.second.second.toFloat()+1),
                        size = Size(gridSizePx-2f, gridSizePx-2f),
                    )
                }
            }
        }
    }

    @Composable
    fun BottomControlsComposable(
        game: Game,
        createShape: GameShape,
        sidePadding: Int,
        gridSizeDp: Int,
        gridSizePx: Int,
        boardSize: Int,
        shapeSize: Int,
        ibShapes: List<ImageBitmap>,
        minShapes: List<Int>,
        selectedColors: List<Color>,
    )
    {
        Box(
            modifier = Modifier
                .offset { IntOffset(createShape.topLeftX, createShape.topLeftY) }
                .padding(sidePadding.dp, 0.dp, sidePadding.dp, 0.dp)
                .width(gridSizeDp.dp * shapeSize)
                .height(gridSizeDp.dp * shapeSize)
                .clip(shape = RoundedCornerShape(20.dp)),
        )
        {
            Canvas(
                modifier = Modifier
                    .fillMaxSize(),
            )
            {
                drawRect(
                    color = Color(android.graphics.Color.parseColor("#E0E0E0")),
                    topLeft = Offset(0f, 0f),
                    size = Size(gridSizePx*shapeSize.toFloat(), gridSizePx*shapeSize.toFloat()),
                )

                for (x in 0..<shapeSize)
                {
                    for (y in 0..<shapeSize)
                    {
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(gridSizePx*x.toFloat()+1, gridSizePx*y.toFloat()+1),
                            size = Size(gridSizePx-2f, gridSizePx-2f),
                        )
                    }
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(sidePadding.dp, 0.dp, sidePadding.dp, 0.dp),
        )
        {
            drawImage(
                image = ibShapes[SHAPES.indexOf(createShape.type)],
                alpha = createShape.alpha,
                dstSize = IntSize(gridSizePx*shapeSize,gridSizePx*shapeSize),
                dstOffset = IntOffset(createShape.topLeftX,createShape.topLeftY),
                colorFilter = ColorFilter.tint(createShape.color),
            )
            if (createShape.pressed)
            {
                drawImage(
                    image = ibShapes[SHAPES.indexOf(createShape.type)],
                    alpha = 0.2f,
                    dstSize = IntSize(gridSizePx*shapeSize,gridSizePx*shapeSize),
                    dstOffset = IntOffset(createShape.topLeftX,createShape.topLeftY),
                    colorFilter = ColorFilter.tint(Color.Black),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(0.dp, gridSizeDp.dp * boardSize + 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
            )
            {
                val prevColorIndex = selectedColors.indexOf(createShape.color) - 1
                val prevColor: Color =
                    if (prevColorIndex < 0)
                    {
                        selectedColors.last()
                    }
                    else
                    {
                        selectedColors[prevColorIndex]
                    }
                InMindSymbolImageButton(
                    imageResource = R.drawable.arrow,
                    rotation = 0f,
                    size = 40,
                    pressedColor = colorResource(R.color.off_white),
                    foregroundImageResource = minShapes[SHAPES.indexOf(createShape.type)],
                    foregroundColor = prevColor,
                    onClick = {
                        createShape.color = prevColor
                    },
                )
            }
            Row(
                modifier = Modifier
                    .offset(0.dp, 10.dp)
                    .height(max(gridSizeDp.dp * shapeSize, 40.dp))
                    .width(max(gridSizeDp.dp * shapeSize, 40.dp) + 100.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                )
                {
                    val prevShape = game.gameConfig.availableTypes.indexOf(createShape.type) - 1
                    val prevShapeName: String =
                        if (prevShape < 0)
                        {
                            game.gameConfig.availableTypes.last()
                        }
                        else
                        {
                            game.gameConfig.availableTypes[prevShape]
                        }

                    InMindSymbolImageButton(
                        imageResource = R.drawable.arrow,
                        rotation = -90f,
                        size = 40,
                        pressedColor = colorResource(R.color.off_white),
                        foregroundImageResource = minShapes[SHAPES.indexOf(prevShapeName)],
                        foregroundColor = createShape.color,
                        onClick = {
                            createShape.type = prevShapeName
                        },
                    )
                }
                Box(
                    modifier = Modifier
                        .offset(10.dp, 0.dp)
                        .height(0.dp)
                        .width(max(gridSizeDp.dp * shapeSize, 40.dp)),
                )
                {

                }
                Box(
                    modifier = Modifier
                        .offset(20.dp, 0.dp)
                        .height(40.dp)
                        .width(40.dp),
                )
                {
                    val nextShape = game.gameConfig.availableTypes.indexOf(createShape.type) + 1
                    val nextShapeName: String =
                        if (nextShape == game.gameConfig.availableTypes.size)
                        {
                            game.gameConfig.availableTypes.first()
                        }
                        else
                        {
                            game.gameConfig.availableTypes[nextShape]
                        }
                    InMindSymbolImageButton(
                        imageResource = R.drawable.arrow,
                        rotation = 90f,
                        size = 40,
                        pressedColor = colorResource(R.color.off_white),
                        foregroundImageResource = minShapes[SHAPES.indexOf(nextShapeName)],
                        foregroundColor = createShape.color,
                        onClick = ({
                            createShape.type = nextShapeName
                        }))
                }
            }
            Row(
                modifier = Modifier
                    .offset(0.dp, 20.dp)
                    .height(40.dp)
                    .width(40.dp),
            )
            {
                val nextColorIndex = selectedColors.indexOf(createShape.color) + 1
                val nextColor: Color =
                    if (nextColorIndex == selectedColors.size)
                    {
                        selectedColors.first()
                    }
                    else
                    {
                        selectedColors[nextColorIndex]
                    }
                InMindSymbolImageButton(
                    imageResource = R.drawable.arrow,
                    rotation = 180f,
                    size = 40,
                    pressedColor = colorResource(R.color.off_white),
                    foregroundImageResource = minShapes[SHAPES.indexOf(createShape.type)],
                    foregroundColor = nextColor,
                    onClick = {
                        createShape.color = nextColor
                    },
                )
            }
        }
    }

    @Composable
    fun MemoryShapesComposable(
        game: Game,
        sidePadding: Int,
        gameStatus: Int,
        gridSizePx: Int,
        ibShapes: List<ImageBitmap>,
        shapeSize: Int,
    )
    {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(sidePadding.dp, 0.dp, sidePadding.dp, 0.dp),
        )
        {
            game.shapes.forEach {
                if (gameStatus!= GAME_STATUS_CHECKING||it.beingChecked)
                {
                    val topLeftX = it.topLeftSquare.first*gridSizePx
                    val topLeftY = it.topLeftSquare.second*gridSizePx
                    drawImage(

                        image = ibShapes[SHAPES.indexOf(it.type)],
                        alpha = if (gameStatus == GAME_STATUS_VIEWING_PLAY) 0.25f else it.alpha,
                        dstSize = IntSize(gridSizePx*shapeSize,gridSizePx*shapeSize),
                        dstOffset = IntOffset(topLeftX,topLeftY),
                        colorFilter = ColorFilter.tint(it.color),
                    )
                }
            }
        }
    }

    @Composable
    fun GameShapesComposable(
        game: Game,
        sidePadding: Int,
        gameStatus: Int,
        createShape: GameShape,
        showBottomMenu: Boolean,
        gridSizePx: Int,
        boardSize: Int,
        shapeSize: Int,
        shapes: MutableList<GameShape>,
        gridPoints: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>,
        ibShapes: List<ImageBitmap>,

        onChangeShowBottomMenu: (Boolean) -> Unit,
    )
    {
        var draggedShape by remember { mutableStateOf<GameShape?>(null) }
        var maxId by remember { mutableIntStateOf(0)}

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(sidePadding.dp, 0.dp, sidePadding.dp, 0.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onDragStart(
                                offset = offset,
                                gameStatus = gameStatus,
                                createShape = createShape,
                                gridSizePx = gridSizePx,
                                boardSize = boardSize,
                                shapeSize = shapeSize,
                                showBottomMenu = showBottomMenu,
                                maxId = maxId,
                                shapes = shapes,
                                onChangeMaxId = {value ->
                                    maxId = value
                                },
                                onChangeDraggedShape = {value ->
                                    draggedShape = value
                                    draggedShape!!.isDragging = true
                                },
                            )
                        },
                        onDrag = { change, _ ->
                            onDrag(
                                change = change,
                                gameStatus = gameStatus,
                                draggedShape = draggedShape,
                                boardSize = boardSize,
                                shapeSize = shapeSize,
                                gridSizePx = gridSizePx,
                                gridPoints = gridPoints,
                                shapes = shapes,
                            )
                        },
                        onDragEnd = {
                            onDragEnd(
                                gameStatus = gameStatus,
                                draggedShape = draggedShape,
                                createShape = createShape,
                                boardSize = boardSize,
                                shapeSize = shapeSize,
                                shapes = shapes,
                                game = game,
                                onChangeShowBottomMenu = {value ->
                                    onChangeShowBottomMenu(value)
                                },
                                onChangeDraggedShape = {value ->
                                    draggedShape = value
                                },
                            )
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            onTap(
                                offset = offset,
                                gameStatus = gameStatus,
                                createShape = createShape,
                                gridSizePx = gridSizePx,
                                shapeSize = shapeSize,
                                showBottomMenu = showBottomMenu,
                                gridPoints = gridPoints,
                                shapes = shapes,
                                game = game,
                                maxId = maxId,
                                onChangeMaxId = {value ->
                                    maxId = value
                                },
                                onChangeShowBottomMenu = {value ->
                                    onChangeShowBottomMenu(value)
                                }
                            )
                        },
                        onPress = { offset ->
                            onPress(
                                offset = offset,
                                gameStatus = gameStatus,
                                gridSizePx = gridSizePx,
                                shapeSize = shapeSize,
                                showBottomMenu = showBottomMenu,
                                createShape = createShape,
                                shapes = shapes,
                            )
                        }
                    )
                },
        )
        {

            shapes.forEach {
                if ((gameStatus!= GAME_STATUS_CHECKING||it.beingChecked)&&!it.removed)
                {
                    if (it.isDragging)
                    {
                        drawRoundRect(
                            color = it.color,
                            alpha = 0.1f,
                            size = Size(gridSizePx*shapeSize.toFloat(),gridSizePx*shapeSize.toFloat()),
                            topLeft = Offset(it.topLeftX.toFloat(),it.topLeftY.toFloat()),
                            cornerRadius = CornerRadius(20.dp.toPx(),20.dp.toPx()),
                        )
                    }

                    drawImage(
                        image = ibShapes[SHAPES.indexOf(it.type)],
                        alpha = if (gameStatus == GAME_STATUS_VIEWING_MEMORY) 0.25f else it.alpha,
                        dstSize = IntSize(gridSizePx*shapeSize,gridSizePx*shapeSize),
                        dstOffset = IntOffset(it.topLeftX,it.topLeftY),
                        colorFilter = ColorFilter.tint(it.color),
                    )

                    if (it.pressed)
                    {
                        drawImage(
                            image = ibShapes[SHAPES.indexOf(it.type)],
                            alpha = 0.2f,
                            dstSize = IntSize(gridSizePx*shapeSize,gridSizePx*shapeSize),
                            dstOffset = IntOffset(it.topLeftX,it.topLeftY),
                            colorFilter = ColorFilter.tint(Color.Black),
                        )
                    }
                }
            }

            shapes.retainAll{!it.removed}
            if (shapes.size<game.gameConfig.totalShapes)
            {
                onChangeShowBottomMenu(true)
            }
        }
    }


    @Composable
    fun GameCheckingComposable(
        sidePadding: Int,
        boardWidthDp: Int,
        currentLevelState: UserLevelAndProgressState,
        selectedColors: List<Color>,
        gameStatus: Int,
        goalShape: GoalState,
        goalColor: GoalState,
        goalLocation: GoalState,
        goalBonus: GoalState,
    )
    {
        Box(
            modifier = Modifier
                .padding(sidePadding.dp, boardWidthDp.dp + 40.dp, sidePadding.dp, 0.dp)
                .fillMaxWidth()
                .height(50.dp),
        ) {
            UserLevelAndProgress(
                currentLevelState = currentLevelState,
                selectedColors = selectedColors,
            )
        }
        Box(
            modifier = Modifier
                .padding(0.dp, boardWidthDp.dp + 120.dp, 0.dp, 0.dp)
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = gameStatus == GAME_STATUS_CHECKING,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.5f),
                )
                {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(sidePadding.dp, 0.dp, sidePadding.dp, 0.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                )
                {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = goalShape.show,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 500)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f),
                        )
                        {
                            InMindImageButton(
                                imageResource = goalShape.icon,
                                defaultColor = goalShape.color,
                                shadowDistance = 5f,
                                isPressable = false,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                )
                {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = goalColor.show,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 500)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f),
                        )
                        {
                            InMindImageButton(
                                imageResource = goalColor.icon,
                                defaultColor = goalColor.color,
                                shadowDistance = 5f,
                                isPressable = false,
                            )
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = goalBonus.show,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 500)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f),
                        )
                        {
                            InMindImageButton(
                                imageResource = goalBonus.icon,
                                defaultColor = goalBonus.color,
                                isPressable = false,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                )
                {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = goalLocation.show,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 500)),
                    )
                    {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f),
                        )
                        {
                            InMindImageButton(
                                imageResource = goalLocation.icon,
                                defaultColor = goalLocation.color,
                                shadowDistance = 5f,
                                isPressable = false,
                            )
                        }
                    }
                }
            }
        }
    }
}