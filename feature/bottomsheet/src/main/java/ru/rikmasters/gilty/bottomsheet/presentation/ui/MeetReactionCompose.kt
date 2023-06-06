package ru.rikmasters.gilty.bottomsheet.presentation.ui

import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
private fun MeetReactionPReview() {
    GiltyTheme {
        MeetReaction(
            DemoFullMeetingModel,
            true,
        ) {}
    }
}

@Composable
fun MeetReaction(
    meet: FullMeetingModel,
    state: Boolean,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    if(state) Popup {
        Box(modifier.fillMaxSize()) {
            GCachedImage(
                url = meet.organizer.avatar
                    ?.thumbnail?.url,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = Crop
            )
            Bottom(
                category = meet.category,
                withoutResponds = meet
                    .withoutResponds,
                modifier = Modifier
                    .align(BottomCenter)
            )
            CloseButton(
                modifier = Modifier
                    .padding(16.dp, 28.dp)
                    .align(TopEnd)
            ) {
                scope.launch {
                    asm.bottomSheet.collapse()
                    onClose()
                }
            }
        }
    }
}

@Composable
private fun Bottom(
    category: CategoryModel,
    withoutResponds: Boolean,
    modifier: Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.22f)
            .clip(shapes.largeTopRoundedShape)
            .background(colorScheme.primaryContainer)
    ) {
        CategoryItem(
            category = category,
            modifier = Modifier
                .align(TopEnd)
                .offset(16.dp, (-20).dp)
        )
        BottomText(withoutResponds)
    }
}


@Composable
private fun CategoryItem(
    category: CategoryModel,
    modifier: Modifier = Modifier,
    size: Dp = CATEGORY_ELEMENT_SIZE.dp,
) {
    Box(
        modifier
            .padding(4.dp)
            .size(size)
    ) {
        Box(
            Modifier
                .size(size)
                .clip(CircleShape)
                .background(category.color)
                .align(BottomCenter),
            Center
        ) {
            Text(
                text = category.name,
                modifier = Modifier.animateContentSize(),
                style = typography.labelSmall.copy(
                    color = Color.White,
                    fontSize = 14.dp.toSp(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
            )
        }
        Box(
            Modifier
                .size((size / 3))
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.BottomStart),
            Center
        ) {
            GEmojiImage(
                emoji = category.emoji,
                modifier = Modifier.size((size / 6))
            )
        }
    }
}


@Composable
private fun BottomText(withoutResponds: Boolean) {
    Column {
        Text(
            stringResource(
                if(withoutResponds) R.string.meeting_without_respond_title
                else R.string.meeting_respond_title
            ), Modifier.padding(top = 16.dp, start = 16.dp),
            color = colorScheme.tertiary,
            style = typography.displayLarge
        )
        Text(
            stringResource(
                if(withoutResponds)
                    R.string.meeting_without_respond_label
                else R.string.meeting_respond_label
            ), Modifier.padding(top = 8.dp, start = 16.dp),
            color = colorScheme.onTertiary,
            style = typography.labelSmall,
            fontWeight = SemiBold
        )
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
                MutableInteractionSource(),
                (null)
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
                colors.meetCloseCrossColor
            )
        }
    }
}