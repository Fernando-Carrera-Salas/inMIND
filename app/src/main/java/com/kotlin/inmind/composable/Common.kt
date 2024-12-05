package com.kotlin.inmind.composable

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kotlin.inmind.composable.Extension.applyIf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Common
{
    @Composable
    fun PressableImage
    (
        size: Int,
        padding: Int,
        defaultImageResource: Int,
        pressedImageResource: Int,
        defaultColor: Color,
        pressedColor: Color,
        defaultAlpha: Float,
        pressedAlpha: Float,
        defaultScale: Float,
        pressedScale: Float,
        defaultRotation: Float,
        pressedRotation: Float,
        defaultXOffset: Float,
        pressedXOffset: Float,
        defaultYOffset: Float,
        pressedYOffset: Float,
        isPressable: Boolean,
        interactionSource: MutableInteractionSource,
    )
    {
        val isPressed =
            if (isPressable)
            {
                interactionSource.collectIsPressedAsState().value
            }
            else
            {
                false
            }

        val animationTransition = updateTransition(isPressed, label = "ClickableTransition")

        val tintFactor by animationTransition.animateColor (
            targetValueByState = { pressed -> if (pressed) pressedColor else defaultColor },
            label = "ClickableColorTransition",
        )
        val alphaFactor by animationTransition.animateFloat(
            targetValueByState = { pressed -> if (pressed) pressedAlpha else defaultAlpha },
            label = "ClickableAlphaTransition",
        )
        val scaleFactor by animationTransition.animateFloat(
            targetValueByState = { pressed -> if (pressed) pressedScale else defaultScale },
            label = "ClickableScaleTransition",
        )
        val rotationFactor by animationTransition.animateFloat(
            targetValueByState = { pressed -> if (pressed) pressedRotation else defaultRotation },
            label = "ClickableRotationTransition",
        )
        val xOffsetFactor by animationTransition.animateFloat(
            targetValueByState = { pressed -> if (pressed) pressedXOffset else defaultXOffset },
            label = "ClickableXOffsetTransition",
        )
        val yOffsetFactor by animationTransition.animateFloat(
            targetValueByState = { pressed -> if (pressed) pressedYOffset else defaultYOffset },
            label = "ClickableYOffsetTransition",
        )

        Image(
            painter = painterResource(id = if (isPressed) pressedImageResource else defaultImageResource),
            contentDescription = null,
            colorFilter = ColorFilter.tint(tintFactor),
            modifier = Modifier
                .applyIf(size==0) { fillMaxSize() }
                .applyIf(size!=0) { size(size.dp) }
                .offset(xOffsetFactor.dp, yOffsetFactor.dp)
                .padding(padding.dp)
                .rotate(rotationFactor)
                .graphicsLayer
                {
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha = alphaFactor
                }
        )
    }




    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ImageButton
                (
        backgroundImage: @Composable (() -> Unit)?,
        mainImage: @Composable (() -> Unit)?,
        foregroundImage: @Composable (() -> Unit)?,
        interactionSource: MutableInteractionSource,
        onLongClick: (() -> Unit)? = null,
        onDoubleClick: (() -> Unit)? = null,
        onClick: (() -> Unit)? = null
    )
    {
        Box(modifier = Modifier
            .applyIf(onClick!=null)
            {
                combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = true,
                    onClick = onClick!!,
                    onLongClick = onLongClick,
                    onDoubleClick = onDoubleClick,
                )
            }
        )
        {
            if (backgroundImage != null)
                backgroundImage()
            if (mainImage != null)
                mainImage()
            if (foregroundImage != null)
                foregroundImage()
        }
    }


    @Composable
    fun InMindImageButton
    (
        imageResource: Int,
        size: Int = 0,
        padding: Int = 0,
        defaultColor: Color = Color.White,
        pressedColor: Color = defaultColor,
        isPressable: Boolean = true,
        shadowDistance: Float = 10f,
        rotation: Float = 0f,
        onClick: (() -> Unit)? = null
    )
    {
        val interactionSource = remember { MutableInteractionSource() }

        ImageButton(
            backgroundImage =
            {
                PressableImage(
                    size = size,
                    padding = padding,
                    defaultImageResource = imageResource,
                    pressedImageResource = imageResource,
                    defaultColor = Color.Black,
                    pressedColor = Color.Black,
                    defaultAlpha = 0.2f,
                    pressedAlpha = 0.1f,
                    defaultScale = 0.9f,
                    pressedScale = 0.8f,
                    defaultRotation = rotation,
                    pressedRotation = rotation,
                    defaultXOffset = 0f,
                    pressedXOffset = 0f,
                    defaultYOffset = shadowDistance,
                    pressedYOffset = shadowDistance/2,
                    isPressable = isPressable,
                    interactionSource = interactionSource
                )
            },
            mainImage =
            {
                PressableImage(
                    size = size,
                    padding = padding,
                    defaultImageResource = imageResource,
                    pressedImageResource = imageResource,
                    defaultColor = defaultColor,
                    pressedColor = pressedColor,
                    defaultAlpha = 1f,
                    pressedAlpha = 1f,
                    defaultScale = 1f,
                    pressedScale = 0.9f,
                    defaultRotation = rotation,
                    pressedRotation = rotation,
                    defaultXOffset = 0f,
                    pressedXOffset = 0f,
                    defaultYOffset = 0f,
                    pressedYOffset = 0f,
                    isPressable = isPressable,
                    interactionSource = interactionSource
                )
            },
            foregroundImage = null,
            interactionSource = interactionSource,
            onClick = onClick
        )
    }

    @Composable
    fun InMindSymbolImageButton
    (
        imageResource: Int,
        foregroundImageResource: Int,
        size: Int = 0,
        padding: Int = 0,
        defaultColor: Color = Color.White,
        pressedColor: Color = defaultColor,
        foregroundColor: Color,
        isPressable: Boolean = true,
        rotation: Float = 0f,
        onClick: (() -> Unit)? = null
    )
    {
        val interactionSource = remember { MutableInteractionSource() }

        ImageButton(
            backgroundImage =
            {
                PressableImage(
                    size = size,
                    padding = padding,
                    defaultImageResource = imageResource,
                    pressedImageResource = imageResource,
                    defaultColor = Color.Black,
                    pressedColor = Color.Black,
                    defaultAlpha = 0.25f,
                    pressedAlpha = 0.5f,
                    defaultScale = 0.9f,
                    pressedScale = 0.8f,
                    defaultRotation = rotation,
                    pressedRotation = rotation,
                    defaultXOffset = 0f,
                    pressedXOffset = 0f,
                    defaultYOffset = 10f,
                    pressedYOffset = 5f,
                    isPressable = isPressable,
                    interactionSource = interactionSource
                )
            },
            mainImage =
            {
                PressableImage(
                    size = size,
                    padding = padding,
                    defaultImageResource = imageResource,
                    pressedImageResource = imageResource,
                    defaultColor = defaultColor,
                    pressedColor = pressedColor,
                    defaultAlpha = 1f,
                    pressedAlpha = 1f,
                    defaultScale = 1f,
                    pressedScale = 0.9f,
                    defaultRotation = rotation,
                    pressedRotation = rotation,
                    defaultXOffset = 0f,
                    pressedXOffset = 0f,
                    defaultYOffset = 0f,
                    pressedYOffset = 0f,
                    isPressable = isPressable,
                    interactionSource = interactionSource
                )
            },
            foregroundImage =
            {
                PressableImage(
                    size = size,
                    padding = padding,
                    defaultImageResource = foregroundImageResource,
                    pressedImageResource = foregroundImageResource,
                    defaultColor = foregroundColor,
                    pressedColor = foregroundColor,
                    defaultAlpha = 1f,
                    pressedAlpha = 1f,
                    defaultScale = 1f,
                    pressedScale = 0.9f,
                    defaultRotation = 0f,
                    pressedRotation = 0f,
                    defaultXOffset = 0f,
                    pressedXOffset = 0f,
                    defaultYOffset = 0f,
                    pressedYOffset = 0f,
                    isPressable = isPressable,
                    interactionSource = interactionSource
                )
            },
            interactionSource = interactionSource,
            onClick = onClick
        )
    }



    @Composable
    fun rememberScreenMeasurements(): ScreenMeasurements
    {
        val configuration = LocalConfiguration.current
        val context = LocalContext.current
        val displayMetrics = context.resources.displayMetrics

        val screenWidthDp = configuration.screenWidthDp
        val screenHeightDp = configuration.screenHeightDp
        val screenHeightPx = displayMetrics.heightPixels
        val screenWidthPx = displayMetrics.widthPixels

        return remember {
            ScreenMeasurements(
                widthDp = screenWidthDp,
                heightDp = screenHeightDp,
                widthPx = screenWidthPx,
                heightPx = screenHeightPx
            )
        }
    }

    data class ScreenMeasurements(
        val widthDp: Int,
        val heightDp: Int,
        val widthPx: Int,
        val heightPx: Int
    )





    @Composable
    fun AnimatedImage(
        frames: List<ImageBitmap>,
        animationSpeed: Int,
        repeatMode: Boolean,
        minWaitTime: Int = 500,
        maxWaitTime: Int = 1500,
        reverse: Boolean = false,
        modifier: Modifier
    ) {
        var currentFrame by remember { mutableIntStateOf(if (reverse) frames.size-1 else 0) }

        var isReversing by remember { mutableStateOf(false) }

        LaunchedEffect(repeatMode, animationSpeed, reverse) {
            if (repeatMode && currentFrame == 0) {
                delay(minWaitTime.toLong())
            }

            while (true) {
                val nextFrame = if (reverse || isReversing) {
                    if (currentFrame > 0) currentFrame - 1 else {
                        isReversing = false
                        if (repeatMode) 1 else 0
                    }
                } else {
                    if (currentFrame < frames.lastIndex) currentFrame + 1 else {
                        isReversing = repeatMode
                        if (repeatMode) frames.lastIndex - 1 else frames.lastIndex
                    }
                }

                currentFrame = nextFrame

                delay(animationSpeed.toLong())

                if (repeatMode && currentFrame == 0) {
                    delay((minWaitTime..maxWaitTime).random().toLong())
                }

                if (!repeatMode && currentFrame == frames.lastIndex) break
            }
        }

        Image(
            bitmap = frames[currentFrame],
            contentDescription = null,
            modifier = modifier
        )
    }






    @Composable
    fun AnimatedGridBackground(fadeIn: Boolean)
    {
        val squareSize = 20.dp

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        val squarePx = with(LocalDensity.current) { squareSize.toPx() }
        val screenWidthPx = with(LocalDensity.current) { screenWidth.toPx() }
        val screenHeightPx = with(LocalDensity.current) { screenHeight.toPx() }

        val columns = (screenWidthPx / squarePx).toInt()
        val rows = (screenHeightPx / squarePx).toInt()

        val gridOpacity = remember { List(rows * columns) { Animatable(0f) } }

        val initialDelay = if (fadeIn) 1000 else 0

        LaunchedEffect(Unit)
        {
            gridOpacity.forEach { animatable ->
                launch {
                    if (!fadeIn)
                        animatable.snapTo( (5..15).random()/100f)
                    while (true) {
                        val delayTime = (0..4000).random().toLong() + initialDelay
                        delay(delayTime)

                        val targetOpacity = (10..30).random()/100f


                        val fadeInDuration = (3000..6000).random()
                        animatable.animateTo(
                            targetValue = targetOpacity,
                            animationSpec = tween(durationMillis = fadeInDuration)
                        )

                        delay((200..500).random().toLong())

                        val fadeOutDuration = (3000..6000).random()
                        animatable.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = fadeOutDuration)
                        )
                    }
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until rows) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    val opacity = gridOpacity[index].value
                    if (opacity > 0f) {
                        drawRect(
                            color = Color.White.copy(alpha = opacity),
                            topLeft = Offset(x = col * squarePx, y = row * squarePx),
                            size = Size(squarePx, squarePx)
                        )
                    }
                }
            }
        }
    }
}