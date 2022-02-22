package com.signaltekno.marqueetext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signaltekno.marqueetext.ui.theme.MarqueeTextTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val myId = "Inline Content"
            val text = buildAnnotatedString {
                append("Do you like Jetpack Compose")
                appendInlineContent(myId, "Icon")
            }

            val inlineIcon = mapOf(
                Pair(
                    myId,
                    InlineTextContent(
                        Placeholder(
                            width = 15.sp,
                            height = 15.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                        )
                    ){
                        Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Love", tint = Color.Red)
                    }
                )
            )
            MarqueeTextTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title ={
                                    Text(text = "Marquee Text and Inline Text")
                                }
                            )
                        },
                        content = {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MarqueenText(
                                    text = "Lorem Ipsum is simply dummy text",
                                    fontSize = 25.sp,
                                    gradientEdgeColor = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    )
                }
            }
        }
    }
}