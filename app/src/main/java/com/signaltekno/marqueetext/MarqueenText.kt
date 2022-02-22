package com.signaltekno.marqueetext

import android.graphics.fonts.FontFamily
import android.graphics.fonts.FontStyle
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun MarqueenText(
    text: String,
    modifier: Modifier = Modifier,
    gradientEdgeColor: Color = Color.White,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val createText = @Composable { localModifier: Modifier ->
        Text(text = text,
            textAlign = textAlign,
            fontSize = fontSize,
            color=color,
            modifier=localModifier,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
            maxLines = 1,
        onTextLayout = onTextLayout,
        style = style)
    }

    var offset by remember{ mutableStateOf(0)}
    var textLayoutInputState by remember {
        mutableStateOf<TextLayoutInfo?>(null)
    }

    LaunchedEffect(key1 = textLayoutInputState){
        textLayoutInputState?.let {
            if(it.textWidth <= it.containerWidth) return@LaunchedEffect
            val duration = 7500 * it.textWidth / it.containerWidth
            val delay = 1000L

            do{
                val animation = TargetBasedAnimation(
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = duration, delayMillis = 1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    typeConverter = Int.VectorConverter,
                    initialValue = 0,
                    targetValue = -(it.textWidth)
                )
                val startTime = withFrameNanos { it }

                do{
                    val playTime = withFrameNanos { it } - startTime
                    offset = (animation.getValueFromNanos(playTime))
                }while(!animation.isFinishedFromNanos(playTime))
                delay(delay)
            }while (true)
        }

    }
    SubcomposeLayout(modifier = modifier.clipToBounds()){ constraints ->
        val infiniteWidthConstraints = constraints.copy(maxWidth = Int.MAX_VALUE)
        var mainText = subcompose(MarqueenLayer.MainText){
            createText(modifier)
        }.first().measure(infiniteWidthConstraints)

        var gradient: Placeable? = null
        var secondPlacebleWithoutOffset: Pair<Placeable, Int>? = null
        if(mainText.width <= constraints.maxWidth){
            mainText = subcompose(MarqueenLayer.SecondaryText){
                createText(Modifier.fillMaxWidth())
            }.first().measure(constraints)
            textLayoutInputState = null
        }else{
            val spacing = constraints.maxWidth * 1 / 5
            textLayoutInputState = TextLayoutInfo(
                textWidth = mainText.width + spacing,
                containerWidth = constraints.maxWidth
            )
            var secondTextOffset = mainText.width + spacing + offset
            val secondTextSpace = constraints.maxWidth - secondTextOffset

            if(secondTextSpace > 0){
                secondPlacebleWithoutOffset = subcompose(MarqueenLayer.SecondaryText){
                    createText(Modifier)
                }.first().measure(infiniteWidthConstraints) to secondTextOffset
            }

            gradient = subcompose(MarqueenLayer.EdgesGradient){
                Row {
                    GradientEdgeColor(startColor = gradientEdgeColor, endColor = Color.Transparent)
                    Spacer(modifier = Modifier.weight(1f))
                    GradientEdgeColor(startColor = Color.Transparent, endColor = gradientEdgeColor)
                }
            }.first().measure(constraints = constraints.copy(maxHeight = mainText.height))
        }

        layout(width = constraints.maxWidth, height = mainText.height){
            mainText.place(offset, 0)
            secondPlacebleWithoutOffset?.let {
                it.first.place(it.second, 0)
            }
            gradient?.place(0,0)
        }
    }
}

@Composable
fun GradientEdgeColor(
    startColor: Color,
    endColor: Color
) {
    Box(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    0f to startColor,
                    1f to endColor
                )
            )
    )
}

enum class MarqueenLayer{
    MainText,
    SecondaryText,
    EdgesGradient
}

data class TextLayoutInfo(
    val textWidth: Int,
    val containerWidth: Int
)