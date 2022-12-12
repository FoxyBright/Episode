package ru.rikmasters.gilty.mainscreen.presentation.ui.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
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
            Modifier.fillMaxSize(),
            contentScale = Crop
        )
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(ThemeExtra.shapes.largeTopRoundedShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column {
                Text(
                    stringResource(R.string.meeting_respond_was_send),
                    Modifier.padding(top = 16.dp, start = 16.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    stringResource(R.string.meeting_wait_organizer_access),
                    Modifier.padding(top = 8.dp, start = 16.dp),
                    color = MaterialTheme.colorScheme.onTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
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
                    .background(MaterialTheme.colorScheme.primaryContainer)
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
