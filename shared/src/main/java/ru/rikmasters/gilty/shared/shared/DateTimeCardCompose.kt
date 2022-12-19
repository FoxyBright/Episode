package ru.rikmasters.gilty.shared.shared

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.common.extentions.dateCalendar
import ru.rikmasters.gilty.shared.common.extentions.timeClock
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.theme.Gradients

@Preview
@Composable
fun TimeCardPreview() {
    DateTimeCard(DemoFullMeetingModel.dateTime, Gradients.green(), true)
}

@Preview
@Composable
fun DateCardPreview() {
    DateTimeCard(DemoFullMeetingModel.dateTime, Gradients.green(), false)
}

@Composable
fun DateTimeCard(
    dateTime: String,
    color: List<Color>,
    today: Boolean,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall
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
            if (today) dateTime.timeClock() else dateTime.dateCalendar(),
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White, fontWeight = FontWeight.SemiBold,
            style = textStyle
        )
    }
}