 package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.enumeration.ConditionType
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra
import ru.rikmasters.gilty.utility.extentions.format


 @Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun TodayMeetingGridCardPreview() {

    GiltyTheme{
        TodayMeetingGridCard(modifier = Modifier.padding(32.dp) ,meeting = DemoMeetingModel) {
        }
    }

}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun TodayMeetingGridPreview() {
    GiltyTheme{
        TodayMeetingGridCompose(Modifier.padding(32.dp), DemoMeetingList) {}
    }
}

@Composable
fun TodayMeetingGridCompose(
    modifier: Modifier = Modifier,
    meetings: List<ShortMeetingModel>,
    onClick: () -> (Unit)
) {

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(meetings) { meeting ->

            TodayMeetingGridCard(meeting = meeting) {

            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayMeetingGridCard(
    modifier: Modifier = Modifier,
    meeting: ShortMeetingModel,
    onClick: () -> (Unit)
) {

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White))
    {

        Text(
            text = meeting.title,
            modifier = Modifier.padding(top = 14.dp, start = 14.dp, end = 14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = ThemeExtra.colors.mainTextColor,
            fontWeight = FontWeight.Bold
        )
        
        Row(modifier = Modifier.padding(bottom = 18.dp)) {
            var gradientColors: List<Color> = listOf(MaterialTheme.colorScheme.primary)
            if (meeting.condition == ConditionType.MEMBER_PAY)
                gradientColors = listOf(
                    colorResource(R.color.green_gradient_1),
                    colorResource(R.color.green_gradient_2),
                    colorResource(R.color.green_gradient_3))
            TimeCardCompose(time = meeting.dateTime, gradientColors = gradientColors)
        }

            AsyncImage(
                model = meeting.organizer.avatar.id,
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(133.dp))

    }

}

@Composable
private fun TimeCardCompose(
    gradientColors: List<Color> = listOf(MaterialTheme.colorScheme.primary), time: String,
) {

    Box(
        Modifier
            .padding(top = 8.dp, start = 14.dp)
            .height(31.dp)
            .background(
                brush = Brush.linearGradient(gradientColors),
                shape = MaterialTheme.shapes.large
            ),
    ) {

        Text(text = time.format("HH:mm"),
            modifier =
            Modifier.padding(
                vertical = 5.dp,
                horizontal = 12.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )

    }

}