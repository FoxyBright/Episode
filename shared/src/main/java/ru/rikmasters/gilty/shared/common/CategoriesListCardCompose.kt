package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
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
    imageSize: Dp = 16.dp,
    shape: Shape = shapes.medium,
) {
    if(border) Box(
        modifier
            .background(
                color = colors.borderColor,
                shape = shape
            )
            .padding(3.dp)
    ) {
        Box(
            Modifier.background(
                color = colorScheme.background,
                shape = shape
            ),
            Center
        ) {
            IconRow(
                modifier = Modifier,
                meeting = meeting,
                imageSize = imageSize
            )
        }
    }
    else Box(modifier, Center) {
        IconRow(
            modifier = Modifier,
            meeting = meeting,
            imageSize = imageSize
        )
    }
}

@Composable
private fun IconRow(
    modifier: Modifier,
    meeting: MeetingModel,
    imageSize: Dp,
) {
    Row(
        modifier,
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