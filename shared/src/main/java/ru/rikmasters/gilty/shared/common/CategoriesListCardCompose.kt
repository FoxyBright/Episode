package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ru.rikmasters.gilty.shared.R.drawable.ic_money
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Composable
fun CategoriesListCard(
    modifier: Modifier,
    meeting: MeetingModel,
    border: Boolean,
    imageSize: Dp = 20.dp,
) {
    Surface(
        modifier, shapes.medium,
        colorScheme.background,
        border = if(border) BorderStroke(
            3.dp, colors.borderColor
        ) else BorderStroke(0.dp, Transparent)
    ) {
        Row(
            Modifier, Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            val emoji = meeting.category.emoji
            Image(
                if(emoji.type == "D")
                    painterResource(emoji.path.toInt())
                else rememberAsyncImagePainter(emoji.path),
                (null),
                Modifier
                    .padding(6.dp)
                    .size(imageSize),
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