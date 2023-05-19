package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCashedImage
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.model.TranslationUiState

@Composable
fun TopActions(
    translationStatus: TranslationStatus,
    onCloseClicked: () -> Unit,
    translationUiState: TranslationUiState,
    remainTime: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (translationStatus == TranslationStatus.PREVIEW) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .height(5.dp)
                    .width(40.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(11.dp),
                color = ThemeExtra.colors.bottomSheetGray
            ) {}
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.translations_preview_title),
                    color = ThemeExtra.colors.white,
                    style = ThemeExtra.typography.TranslationTitlePreview
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_translations),
                    contentDescription = "Close preview",
                    tint = Color.Unspecified,
                    modifier = Modifier.clickable {
                        onCloseClicked()
                    }
                )
            }
        } else {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    translationUiState.meetingModel?.let {
                        GCashedImage(
                            url = it.organizer.avatar?.thumbnail?.url,
                            modifier = Modifier
                                .size(41.dp)
                                .clip(CircleShape)
                                .clickable { onCloseClicked() },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(11.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${it.organizer.username}, ${it.organizer.age}",
                                    color = ThemeExtra.colors.white,
                                    style = ThemeExtra.typography.TranslationSmallButton
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                it.organizer.emoji?.let {
                                    Image(
                                        painter = painterResource(id = EmojiModel.getEmoji(it.type).path.toInt()),
                                        contentDescription = "Emoji"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(0.5.dp))
                            Text(
                                text = it.category.name,
                                color = ThemeExtra.colors.white,
                                style = ThemeExtra.typography.TranslationSmallButton
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ThemeExtra.colors.thirdOpaqueGray
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                start = 5.dp,
                                end = 8.dp,
                                top = 6.5.dp,
                                bottom = 6.5.dp
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_timer_clock),
                                contentDescription = "timer",
                                tint = ThemeExtra.colors.white
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = remainTime,
                                color = ThemeExtra.colors.white,
                                style = ThemeExtra.typography.TranslationSmallButton
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_translations),
                        contentDescription = "Close preview",
                        tint = Color.Unspecified,
                        modifier = Modifier.clickable {
                            onCloseClicked()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiddleActions(
    usersCount: Int,
    onChatCLicked: () -> Unit,
    onUsersClicked: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chat),
            contentDescription = "Chat",
            tint = ThemeExtra.colors.white,
            modifier = Modifier.clickable { onChatCLicked() }
        )
        BadgedBox(
            badge = {
                if (usersCount > 0) {
                    Badge(
                        containerColor = ThemeExtra.colors.mainDayGreen,
                        contentColor = ThemeExtra.colors.white
                    ) {
                        Text(
                            text = usersCount.toString(),
                            style = ThemeExtra.typography.TranslationBadge,
                            color = ThemeExtra.colors.white
                        )
                    }
                }
            },
            modifier = Modifier.clickable { onUsersClicked() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Chat",
                tint = ThemeExtra.colors.white
            )
        }
    }
}

@Composable
fun BottomActions(
    startBroadCast: () -> Unit,
    translationUiState: TranslationUiState,
    changeFacing: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier
                    .clickable {
                        changeFacing()
                    },
                color = if (translationUiState.selectedCamera == Facing.FRONT) {
                    ThemeExtra.colors.thirdOpaqueGray
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        vertical = 6.dp,
                        horizontal = 16.dp
                    ),
                    text = stringResource(id = R.string.translations_front_camera),
                    color = ThemeExtra.colors.white,
                    style = ThemeExtra.typography.TranslationSmallButton
                )
            }
            Surface(
                modifier = Modifier
                    .clickable {
                        changeFacing()
                    },
                color = if (translationUiState.selectedCamera == Facing.BACK) {
                    ThemeExtra.colors.thirdOpaqueGray
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        vertical = 6.dp,
                        horizontal = 16.dp
                    ),
                    text = stringResource(id = R.string.translations_main_camera),
                    color = ThemeExtra.colors.white,
                    style = ThemeExtra.typography.TranslationSmallButton
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier
                .clickable {
                    startBroadCast()
                }
                .fillMaxWidth()
                .background(
                    linearGradient(green()),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 16.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        linearGradient(green())
                    ),
                text = stringResource(id = R.string.translations_start_strean),
                color = ThemeExtra.colors.white,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(53.dp))
    }
}

@Composable
fun BottomActionsExpired(
    onExtendCLicked:() -> Unit
){
    Surface(
        modifier = Modifier
            .clickable {
                onExtendCLicked()
            }
            .fillMaxWidth()
            .background(
                linearGradient(green()),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    linearGradient(green())
                ),
            text = stringResource(id = R.string.translations_start_strean),
            color = ThemeExtra.colors.white,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
    Spacer(modifier = Modifier.height(53.dp))
}