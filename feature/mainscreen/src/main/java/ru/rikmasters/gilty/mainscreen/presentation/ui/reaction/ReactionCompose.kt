package ru.rikmasters.gilty.mainscreen.presentation.ui.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.shared.CategoryItem
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun ReactionPreview() {
    GiltyTheme { ReactionContent(DemoAvatarModel.id) {} }
}

@Composable
fun ReactionContent(
    avatar: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Box(modifier.fillMaxSize()) {
        AsyncImage(
            avatar,
            stringResource(R.string.meeting_avatar),
            Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(ThemeExtra.shapes.largeTopRoundedShape)
                .background(ThemeExtra.colors.cardBackground)
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
        Box(
            Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        ) {
            IconButton(
                onBack, Modifier
                    .padding(16.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(ThemeExtra.colors.cardBackground)
            ) {
                Icon(
                    Icons.Filled.Close,
                    null,
                    Modifier.size(20.dp)
                )
            }
        }
    }
}
