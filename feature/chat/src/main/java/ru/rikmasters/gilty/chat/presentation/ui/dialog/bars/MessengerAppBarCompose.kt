package ru.rikmasters.gilty.chat.presentation.ui.dialog.bars

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.PinnedBarType.*
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.WordEndingType.PARTICIPANT
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.WordEndingType.VIEWER
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun ChatAppBarPreview() {
    GiltyTheme {
        ChatAppBarContent(
            ChatAppBarState(
                ("Бэтмен"), DemoAvatarModel, (4),
                MEET_FINISHED, isOnline = false,
                isOrganizer = true
            )
        )
    }
}

@Preview
@Composable
private fun PinnedBarPreview() {
    GiltyTheme {
        Column(Modifier.background(colorScheme.background)) {
            PinnedBar(MEET_FINISHED, isOnline = true)
            PinnedBar(TRANSLATION, 43, true)
            PinnedBar(
                TRANSLATION_AWAIT, (0),
                true, ("04:53")
            )
        }
    }
}

data class ChatAppBarState(
    val name: String,
    val avatar: AvatarModel?,
    val memberCount: Int,
    val chatType: PinnedBarType,
    val viewer: Int? = null,
    val toTranslation: String? = null,
    val isOnline: Boolean,
    val isOrganizer: Boolean,
)

interface ChatAppBarCallback {
    
    fun onBack() {}
    fun onTopBarClick() {}
    fun onKebabClick() {}
    fun onPinnedBarButtonClick() {}
}

@Composable
fun ChatAppBarContent(
    state: ChatAppBarState,
    modifier: Modifier = Modifier,
    callback: ChatAppBarCallback? = null,
) {
    Column(modifier.background(colorScheme.background)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { callback?.onTopBarClick() }
                .padding(top = 24.dp, bottom = 14.dp),
            Start, CenterVertically
        ) {
            IconButton({ callback?.onBack() }) {
                Icon(
                    painterResource(ic_back),
                    (null), Modifier.size(24.dp),
                    colorScheme.tertiary
                )
            }
            Row(
                Modifier.weight(1f), Start,
                CenterVertically
            ) {
                Information(
                    Modifier
                        .padding(start = 10.dp),
                    state
                )
            }
            IconButton({ callback?.onKebabClick() }) {
                Icon(
                    painterResource(ic_kebab),
                    (null), Modifier.size(16.dp),
                    colorScheme.tertiary
                )
            }
        }
        if(state.chatType != MEET) PinnedBar(
            state.chatType, state.viewer,
            state.isOnline, state.toTranslation
        ) { callback?.onPinnedBarButtonClick() }
    }
}

enum class PinnedBarType {
    MEET, MEET_FINISHED,
    TRANSLATION, TRANSLATION_AWAIT
}

@Composable
private fun PinnedBar(
    type: PinnedBarType,
    viewer: Int? = null,
    isOnline: Boolean,
    toTranslation: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Card(
        Modifier, RectangleShape,
        cardColors(colorScheme.background),
        cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            SpaceBetween, CenterVertically
        ) {
            Row(
                Modifier, Start,
                if(type == TRANSLATION) Top
                else CenterVertically
            ) {
                if(type != MEET_FINISHED) Image(
                    painterResource(ic_video_active),
                    (null), Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                )
                PinedText(
                    type, viewer,
                    Modifier.padding()
                )
            }
            PinedButton(type, isOnline, toTranslation)
            { onClick?.let { it() } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinedButton(
    type: PinnedBarType,
    isOnline: Boolean,
    toTranslation: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val viewString = stringResource(R.string.chats_view_translation_button)
    val meetType = type == MEET_FINISHED
    Card(
        { onClick?.let { it() } },
        Modifier, (type != TRANSLATION_AWAIT
                || toTranslation == null),
        shapes.large, cardColors(Transparent)
    ) {
        Box(
            Modifier.background(
                linearGradient(
                    if(meetType) listOf(
                        colors.chipGray,
                        colors.chipGray
                    ) else green()
                ), shapes.large
            )
        ) {
            Row(
                Modifier.padding(12.dp, 8.dp),
                Start, CenterVertically
            ) {
                if(type == TRANSLATION_AWAIT) Icon(
                    painterResource(ic_timer),
                    (null), Modifier
                        .padding(end = 4.dp)
                        .size(16.dp), White
                )
                Text(
                    when(type) {
                        MEET_FINISHED -> stringResource(R.string.chats_close_chat_pinned_bar_button)
                        TRANSLATION -> viewString
                        TRANSLATION_AWAIT -> toTranslation ?: viewString
                        
                        else -> ""
                    }, Modifier.padding(), if(meetType)
                        if(isOnline) colorScheme.secondary
                        else colorScheme.primary else White,
                    fontWeight = SemiBold,
                    style = typography.labelSmall
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalTextApi::class)
private fun PinedText(
    type: PinnedBarType,
    viewer: Int?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            stringResource(
                when(type) {
                    MEET_FINISHED -> R.string.chats_meet_ended_pinned_bar_label
                    TRANSLATION_AWAIT -> R.string.chats_translation_await_pinned_bar_label
                    TRANSLATION -> R.string.chats_translation_pinned_bar_label
                    else -> 0
                }
            ), style = typography.labelSmall.copy(
                brush = linearGradient(
                    if(type == TRANSLATION) green()
                    else listOf(
                        colorScheme.tertiary,
                        colorScheme.tertiary
                    )
                ), fontWeight = SemiBold
            )
        )
        viewer?.let {
            if(it > 0) Text(
                "$it ${
                    stringResource(
                        R.string.chats_viewer_cases,
                        wordEnding(VIEWER, it)
                    )
                }", Modifier, colorScheme.tertiary,
                style = typography.headlineSmall,
                fontWeight = SemiBold
            )
        }
    }
}

@Composable
private fun Information(
    modifier: Modifier = Modifier,
    state: ChatAppBarState,
) {
    Row(modifier, verticalAlignment = CenterVertically) {
        AsyncImage(
            state.avatar?.thumbnail?.url,
            stringResource(R.string.meeting_avatar),
            Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                state.name,
                Modifier.padding(start = 12.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium,
                fontWeight = SemiBold
            )
            val count = state.memberCount + 1
            Text(
                "$count ${
                    stringResource(
                        R.string.chats_member_cases,
                        wordEnding(PARTICIPANT, count)
                    )
                }",
                Modifier.padding(start = 12.dp),
                colorScheme.onTertiary,
                style = typography.labelSmall,
            )
        }
    }
}

private enum class WordEndingType {
    VIEWER, PARTICIPANT
}

private fun wordEnding(
    type: WordEndingType,
    count: Int,
): String {
    val viewer = (type == VIEWER)
    return when(("$count".last())) {
        '1' -> if(viewer) "ь" else ""
        '2', '3', '4' -> if(viewer) "я" else "а"
        else -> if(viewer) "ей" else "ов"
    }
}