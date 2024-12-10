package com.kotlin.inmind.composable

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.inmind.R
import com.kotlin.inmind.composable.Common.AnimatedGridBackground
import com.kotlin.inmind.composable.Common.InMindImageButton
import com.kotlin.inmind.composable.Common.rememberScreenMeasurements
import com.kotlin.inmind.composable.Common.ScreenMeasurements
import com.kotlin.inmind.util.Util
import kotlinx.coroutines.delay

object SplashScreen
{
    @Composable
    fun SplashScreenComposable(
        onPlayClick: () -> Unit,
        onColorWheelSelected: (List<Color>) -> Unit,
        currentColors: List<Color>,
        skipSplash: Boolean,
        versionCode: String,
    )
    {
        val context = LocalContext.current
        val screenMeasurements = rememberScreenMeasurements()

        var showLogo by remember { mutableStateOf(false) }
        var showButtons by remember { mutableStateOf(false) }
        var showBackground by remember { mutableStateOf(false) }
        var showOptions by remember { mutableStateOf(false) }

        var isAnimationSkipped by remember { mutableStateOf(skipSplash) }

        val colorOptions = Util.loadColorOptions(context)
        var selectedColorsIndex by remember { mutableStateOf<Int?>(0) }

        var backgroundColors = remember { currentColors.toMutableList() }

        var colorIndex by remember { mutableIntStateOf(0) }
        var changedColor by remember { mutableStateOf(false)}

        if (!changedColor)
        {
            selectedColorsIndex = colorOptions.indexOf(currentColors)
            if (selectedColorsIndex==-1)
            {
                selectedColorsIndex = 0
            }
        }

        if (selectedColorsIndex!=0)
        {
            backgroundColors = colorOptions[selectedColorsIndex!!].toMutableList()
        }

        val animatedColor = remember { Animatable(backgroundColors.first()) }

        LaunchedEffect(backgroundColors)
        {
            if (!isAnimationSkipped)
            {
                delay(500)
                showBackground = true

                delay(1500)
                showLogo = true

                delay(1000)
                showButtons = true
            }
            else
            {
                showBackground = true
                showLogo = true
                showButtons = true
            }

            while (true)
            {
                val nextIndex = (colorIndex + 1) % backgroundColors.size
                animatedColor.animateTo(
                    targetValue = backgroundColors[nextIndex],
                    animationSpec = tween(durationMillis = 2000),
                )
                colorIndex = nextIndex
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.white))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                )
                {
                    isAnimationSkipped = true
                    showBackground = true
                    showLogo = true
                    showButtons = true
                }
        )
        {
            AnimatedVisibility(
                visible = showBackground,
                enter =
                    if (!isAnimationSkipped)
                    {
                        fadeIn(animationSpec = tween(durationMillis = 1500))
                    }
                    else
                    {
                        EnterTransition.None
                    },
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.light_gray)),
                )
                {
                    AnimatedGridBackground(!skipSplash)
                }
            }
            AnimatedVisibility(
                visible = !showOptions,

                enter = slideInVertically(
                    initialOffsetY = { -screenMeasurements.heightPx },
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing),
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -screenMeasurements.heightPx },
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing),
                ),
            ) {
                MenuComposable(
                    showLogo = showLogo,
                    showButtons = showButtons,
                    isAnimationSkipped = isAnimationSkipped,
                    animatedColor = animatedColor,
                    screenMeasurements = screenMeasurements,
                    versionCode = versionCode,
                    onPlayClick = onPlayClick,
                    onChangeShowOptions = { value ->
                        showOptions = value
                    },
                )
            }

            AnimatedVisibility(
                visible = showOptions,

                enter = slideInVertically(
                    initialOffsetY = { screenMeasurements.heightPx },
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing),
                ),
                exit = slideOutVertically(
                    targetOffsetY = { screenMeasurements.heightPx },
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing),
                ),
            ) {
                OptionsComposable(
                    colorOptions = colorOptions,
                    selectedOptionIndex = selectedColorsIndex,
                    onSelectOption = { selectedIndex ->
                        changedColor = true
                        selectedColorsIndex = selectedIndex
                        onColorWheelSelected(colorOptions[selectedIndex])
                    },
                    onBack = {showOptions = false},
                )
            }
        }

        BackHandler {
            if (showOptions)
            {
                showOptions = false
            }
        }
    }

    @Composable
    fun MenuComposable(
        showLogo: Boolean,
        showButtons: Boolean,
        isAnimationSkipped: Boolean,
        animatedColor: Animatable<Color,AnimationVector4D>,
        screenMeasurements: ScreenMeasurements,
        versionCode: String,

        onPlayClick: () -> Unit,
        onChangeShowOptions: (Boolean) -> Unit,
    )
    {

        Box(
            modifier = Modifier
                .fillMaxSize(),
        )
        {
            AnimatedVisibility(
                visible = showLogo,
                enter =
                    if (!isAnimationSkipped)
                    {
                        slideInHorizontally(
                            initialOffsetX = {-screenMeasurements.widthPx},
                            animationSpec = tween(durationMillis = 1000)
                        )
                    }
                    else
                    {
                        EnterTransition.None
                    },
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                )
                {
                    Image(
                        painter = painterResource(id = R.drawable.logo_bg),
                        contentDescription = "LogoBackground",
                        colorFilter = ColorFilter.tint(animatedColor.value),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            AnimatedVisibility(
                visible = showButtons,
                enter =
                    if (!isAnimationSkipped)
                    {
                        fadeIn(animationSpec = tween(durationMillis = 1500))
                    }
                    else
                    {
                        EnterTransition.None
                    },
            )
            {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                )
                {
                    InMindImageButton(
                        imageResource = R.drawable.play,
                        size = 100,
                        padding = 10,
                        pressedColor = colorResource(R.color.im_green),
                        onClick = onPlayClick,
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    InMindImageButton(
                        imageResource = R.drawable.config,
                        size = 100,
                        padding = 20,
                        pressedColor = colorResource(R.color.im_blue),
                        onClick = ({
                            onChangeShowOptions(true)
                        }),
                    )

                    Spacer(
                        modifier = Modifier.height(60.dp)
                    )

                    Text(
                        text = versionCode,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        fontSize = 10.sp,
                        color = Color.White,
                        textAlign = TextAlign.End,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(1f, 1f),
                                    blurRadius = 5f,
                                )
                            ),
                        ),
                    )
                }
            }
        }
    }


    @Composable
    fun OptionsComposable(
        colorOptions: List<List<Color>>,
        selectedOptionIndex: Int?,
        onSelectOption: (Int) -> Unit,
        onBack: () -> Unit,
    )
    {
        val contentPadding = 20.dp
        val spacing = 20.dp
        val itemWidth = 100.dp

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        )
        {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                columns = GridCells.Adaptive(itemWidth),
                contentPadding = PaddingValues(contentPadding),
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalArrangement = Arrangement.spacedBy(spacing),
            )
            {
                item(span = { GridItemSpan(maxLineSpan) })
                {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center,
                    )
                    {
                        InMindImageButton(
                            imageResource = R.drawable.back_arrow,
                            rotation = 90f,
                            size = itemWidth.value.toInt()/2,
                            pressedColor = colorResource(R.color.im_red),
                            onClick = onBack,
                        )
                    }
                }
                items(colorOptions.size)
                { index ->
                    val isSelected = selectedOptionIndex == index
                    ColorWheelOption(
                        colors = colorOptions[index],
                        isSelected = isSelected,
                        onClick = {
                            onSelectOption(index)
                        },
                    )
                }
            }
        }
    }


    @Composable
    fun ColorWheelOption(
        colors: List<Color>,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(10.dp)
                .background(
                    color = if (isSelected) Color.White else Color.Transparent,
                    shape = CircleShape
                )
                .clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(false),
                )
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isSelected) 10.dp else 16.dp)
            ) {
                val radius = size.minDimension / 1.8f
                colors.forEachIndexed { i, color ->
                    drawArc(
                        color = color,
                        startAngle = (360f / colors.size) * i,
                        sweepAngle = 360f / colors.size,
                        useCenter = true,
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(
                            (size.width - radius * 2) / 2,
                            (size.height - radius * 2) / 2
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .background(
                        color = if (isSelected) Color.White else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}