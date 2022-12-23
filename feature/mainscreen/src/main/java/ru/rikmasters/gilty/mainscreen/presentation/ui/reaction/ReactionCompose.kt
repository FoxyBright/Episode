package ru.rikmasters.gilty.mainscreen.presentation.ui.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType.ENTERTAINMENT
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
fun ReactionPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            ReactionContent(
                DemoAvatarModel.id,
                ENTERTAINMENT
            )
        }
    }
}

@Composable
fun ReactionContent(
    avatar: String,
    categoriesType: CategoriesType,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Box(modifier.fillMaxSize()) {
        AsyncImage(
            avatar, stringResource(R.string.meeting_avatar),
            Modifier.fillMaxSize(),
            contentScale = Crop
        )
        Box(
            Modifier
                .fillMaxWidth()
                .align(BottomCenter)
                .clip(shapes.largeTopRoundedShape)
                .background(colorScheme.primaryContainer)
        ) {
            Column {
                Text(
                    stringResource(R.string.meeting_respond_was_send),
                    Modifier.padding(top = 16.dp, start = 16.dp),
                    color = colorScheme.tertiary,
                    style = typography.displayLarge
                )
                Text(
                    stringResource(R.string.meeting_wait_organizer_access),
                    Modifier.padding(top = 8.dp, start = 16.dp),
                    color = colorScheme.onTertiary,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
            CategoryItem(
                categoriesType, (true), Modifier
                    .align(TopEnd)
                    .offset(12.dp, (-18).dp)
            )
        }
        Box(
            Modifier
                .padding(16.dp)
                .align(TopEnd)
        ) {
            IconButton(
                { onBack?.let { it() } }, Modifier
                    .padding(16.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primaryContainer)
            ) {
                Icon(
                    Filled.Close, (null),
                    Modifier.size(20.dp)
                )
            }
        }
    }
}
