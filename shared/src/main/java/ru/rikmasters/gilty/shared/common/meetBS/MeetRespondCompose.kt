package ru.rikmasters.gilty.shared.common.meetBS

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
fun MeetRespondPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetRespondContent(
                DemoAvatarModel,
                DemoCategoryModel,
                false,
            )
        }
    }
}

@Composable
fun MeetRespondContent(
    avatar: AvatarModel?,
    categoryModel: CategoryModel,
    withoutResponse: Boolean,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    Box(modifier.fillMaxSize()) {
        AsyncImage(
            avatar?.url, (null),
            Modifier.fillMaxSize(),
            contentScale = Crop
        )
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(BottomCenter)
                .clip(shapes.largeTopRoundedShape)
                .background(colorScheme.primaryContainer)
        ) {
            Column {
                Text(
                    stringResource(
                        if(withoutResponse) R.string.meeting_without_respond_title
                        else R.string.meeting_respond_title
                    ), Modifier.padding(top = 16.dp, start = 16.dp),
                    color = colorScheme.tertiary,
                    style = typography.displayLarge
                )
                Text(
                    stringResource(
                        if(withoutResponse) R.string.meeting_without_respond_label
                        else R.string.meeting_respond_label
                    ), Modifier.padding(top = 8.dp, start = 16.dp),
                    color = colorScheme.onTertiary,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
            CategoryItem(
                categoryModel.name,
                categoryModel.emoji,
                categoryModel.color,
                (true), Modifier
                    .align(TopEnd)
                    .offset(12.dp, (-18).dp)
            )
        }
        CloseButton(
            Modifier
                .padding(16.dp, 28.dp)
                .align(TopEnd)
        ) { onBack?.let { it() } }
    }
}

@Composable
private fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .clip(CircleShape)
            .clickable(
                MutableInteractionSource(), (null)
            ) { onClick() }, Center
    ) {
        Box(
            Modifier
                .height(Max)
                .width(Max)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        colors.meetCloseCircleColor,
                        CircleShape
                    )
            )
            Icon(
                painterResource(R.drawable.ic_bold_cross),
                (null), Modifier
                    .padding(10.dp)
                    .size(10.dp),
                White
            )
        }
    }
}