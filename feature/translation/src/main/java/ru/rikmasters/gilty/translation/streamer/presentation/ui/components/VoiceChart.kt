package ru.rikmasters.gilty.translation.streamer.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VoiceLevelChart(
    audioLevel: Double
) {
    val chartHeight = 200.dp
    val chartWidth = 300.dp
    val chartColor = Color.Red

    // Scale the amplitude to fit within the chart height
    val chartAmplitude = (audioLevel * chartHeight.value).coerceIn(0.0, chartHeight.value.toDouble())

    Canvas(modifier = Modifier.size(chartWidth, chartHeight)) {
        // Draw the chart background
        drawRect(color = Color.LightGray,size =size)
        // Draw the chart amplitude
        drawRect(chartColor, Offset(0f, (chartHeight.value - chartAmplitude).toFloat()), Size(chartWidth.value,
            chartAmplitude.toFloat()
        ))
    }
}