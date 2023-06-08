package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.ic_money
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Composable
fun CategoriesListCard(
    modifier: Modifier,
    meeting: MeetingModel,
    border: Boolean = false,
    imageSize: Dp = 20.dp,
    shape: Shape = shapes.medium,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Transparent
        ),
        border = if(border) BorderStroke(
            3.dp, colors.borderColor
        ) else BorderStroke(0.dp, Transparent)
    ) {
        Row(
            Modifier.background(
                colorScheme.background
            ),
            SpaceEvenly,
            CenterVertically
        ) {
            GEmojiImage(
                meeting.category.emoji,
                Modifier
                    .padding(6.dp)
                    .size(imageSize)
            )
            if(meeting.condition == MEMBER_PAY) Image(
                painterResource(ic_money),
                (null), Modifier
                    .padding(6.dp)
                    .size(imageSize)
            )
        }
    }
}