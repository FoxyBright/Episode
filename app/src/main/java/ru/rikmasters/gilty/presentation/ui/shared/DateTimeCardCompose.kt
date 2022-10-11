package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra
import ru.rikmasters.gilty.utility.extentions.format

@Composable
fun DateTimeCard(
    dateTime: String,
    color: List<Color>,
    today: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .background(
                Brush.linearGradient(color),
                MaterialTheme.shapes.large
            ),
        Alignment.Center
    ) {
        Text(
            dateTime.format(if (today) "HH:mm" else "dd MMMM"),
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = ThemeExtra.typography.SubHeadEb,
            color = Color.White
        )
    }
}