package ru.rikmasters.gilty.core.app.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign.Companion.TextCenter
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.core.R

@Preview
@Composable
private fun ErrorPReview() {
    Box(Modifier.fillMaxSize()) {
        ErrorConnection()
    }
}

@SuppressLint("InvalidColorHexValue")
@Composable
fun ErrorConnection() {
    Box(
        Modifier
            .fillMaxHeight(0.9f)
            .fillMaxWidth()
    ) {
        Box(
            Modifier
                .padding(horizontal = 8.dp)
                .align(BottomCenter)
        ) {
            Box(
                Modifier
                    .height(54.dp)
                    .fillMaxWidth()
                    .background(
                        Color(0xE60E0E0E),
                        RoundedCornerShape(14.dp)
                    )
            )
            Text(
                buildAnnotatedString {
                    appendInlineContent("emoji")
                    append("  Отсутствует подключение к интернету")
                }, Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 16.dp), White,
                style = typography.labelSmall
                    .copy(lineHeight = 48.sp),
                inlineContent = mapOf(
                    "emoji" to InlineTextContent(
                        Placeholder(28.sp, 28.sp, TextCenter)
                    ) {
                        Image(
                            painterResource(R.drawable.ic_information),
                            (null), Modifier
                        )
                    }
                ), textAlign = Center
            )
        }
    }
}