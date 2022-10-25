package ru.rikmasters.gilty.presentation.recommendation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModel
import ru.rikmasters.gilty.shared.shared.CategoryItem
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun SendReactionBottomComposePreview() {
    GiltyTheme {
        SendReactionBottomCompose()
    }
}

@Composable
fun SendReactionBottomCompose(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp, 14.dp))
    ) {
        Column {
            Text(
                stringResource(R.string.meeting_respond_was_send),
                Modifier.padding(top = 16.dp, start = 16.dp),
                color = ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H2
            )
            Text(
                stringResource(R.string.meeting_wait_organizer_access),
                Modifier.padding(top = 8.dp, start = 16.dp),
                color = ThemeExtra.colors.secondaryTextColor,
                style = ThemeExtra.typography.SubHeadSb
            )
        }
        CategoryItem(
            DemoShortCategoryModel,
            true,
            Modifier
                .align(Alignment.TopEnd)
                .offset(20.dp, (-20).dp)
        )
    }
}
