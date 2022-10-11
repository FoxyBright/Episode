package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.enumeration.ConditionType
import ru.rikmasters.gilty.presentation.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Composable
fun categoriesListCard(modifier: Modifier, meeting: ShortMeetingModel, border: Boolean) {
    Surface(
        modifier,
        MaterialTheme.shapes.medium,
        MaterialTheme.colorScheme.background,
        border = if (border) BorderStroke(
            3.dp,
            ThemeExtra.colors.borderColor
        ) else BorderStroke(0.dp, Color.Transparent)
    ) {
        Row(
            Modifier,
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            AsyncImage(
                meeting.category.emoji.path,
                stringResource(R.string.next_button),
                Modifier
                    .padding(6.dp)
                    .size(20.dp),
                painterResource(R.drawable.cinema)
            )
            if (meeting.condition == ConditionType.MEMBER_PAY)
                AsyncImage(
                    meeting.category.emoji.path,
                    stringResource(R.string.next_button),
                    Modifier
                        .padding(6.dp)
                        .size(20.dp),
                    painterResource(R.drawable.cinema)
                )
        }
    }
}