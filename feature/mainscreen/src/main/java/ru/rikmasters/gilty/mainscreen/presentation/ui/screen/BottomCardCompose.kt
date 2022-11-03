package ru.rikmasters.gilty.mainscreen.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.categoriesListCard
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.DateTimeCard
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
fun CardButtonPreview() {
    GiltyTheme {
        CardButton(
            Modifier,
            stringResource(R.string.meeting_respond),
            false, R.drawable.ic_heart
        ) {}
    }
}


@Preview
@Composable
fun MeetingStatesPreview() {
    GiltyTheme {
        MeetingStates(
            Modifier,
            DemoFullMeetingModel
        )
    }
}

@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    text: String,
    online: Boolean,
    icon: Int,
    onClick: () -> Unit,
) {
    val color = if (online)
        colorScheme.secondary
    else colorScheme.primary
    Button(
        onClick,
        modifier,
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding = PaddingValues(12.dp, 10.dp),
        colors = ButtonDefaults.buttonColors(ThemeExtra.colors.grayButton)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(icon), (null),
                Modifier.padding(end = 2.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Text(
                text, Modifier.padding(top = 2.dp), color,
                style = MaterialTheme
                    .typography.labelSmall
                    .copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun MeetingStates(modifier: Modifier, meet: FullMeetingModel) {
    val today = todayControl(meet.dateTime)
    Box(modifier) {
        Row(Modifier.align(Alignment.CenterEnd)) {
            Column(horizontalAlignment = Alignment.End) {
                DateTimeCard(
                    meet.dateTime,
                    if (meet.isOnline) Gradients.green()
                    else Gradients.red(), today
                )
                if (!today && meet.condition == ConditionType.MEMBER_PAY)
                    Icons(Modifier.padding(top = 8.dp), meet)
            }
            if (today || meet.condition != ConditionType.MEMBER_PAY)
                Icons(Modifier.padding(start = 4.dp), meet)
        }
    }
}

@Composable
private fun Icons(modifier: Modifier, meet: FullMeetingModel) {
    categoriesListCard(modifier, meet, (false))
}