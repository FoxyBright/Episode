package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun categoriesListCard(
    modifier: Modifier,
    meeting: FullMeetingModel,
    border: Boolean
) {
    Surface(
        modifier,
        MaterialTheme.shapes.medium,
        MaterialTheme.colorScheme.background,
        border = if (border) BorderStroke(
            3.dp, ThemeExtra.colors.borderColor
        ) else BorderStroke(0.dp, Color.Transparent)
    ) {
        Row(
            Modifier, Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.ic_fire), (null),
                Modifier
                    .padding(6.dp)
                    .size(20.dp),
            )
            if (meeting.condition == ConditionType.MEMBER_PAY)
                Image(
                    painterResource(R.drawable.ic_money), (null),
                    Modifier
                        .padding(6.dp)
                        .size(20.dp)
                )
        }
    }
}