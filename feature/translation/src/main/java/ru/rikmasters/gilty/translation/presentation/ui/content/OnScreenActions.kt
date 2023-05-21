package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.model.TranslationUiState
import ru.rikmasters.gilty.translation.presentation.ui.components.CameraOrientationRow
import ru.rikmasters.gilty.translation.presentation.ui.components.ChatItem
import ru.rikmasters.gilty.translation.presentation.ui.components.CloseButton
import ru.rikmasters.gilty.translation.presentation.ui.components.MembersCountItem
import ru.rikmasters.gilty.translation.presentation.ui.components.StreamerItem
import ru.rikmasters.gilty.translation.presentation.ui.components.TimerItem

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
                CloseButton { onCloseClicked() }
            }
        } else {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                translationUiState.meetingModel?.let {
                    StreamerItem(meetingModel = it)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimerItem(time = remainTime)
                    Spacer(modifier = Modifier.width(12.dp))
                    CloseButton { onCloseClicked() }
                }
            }
        }
    }
}

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
        ChatItem { onChatCLicked() }
        MembersCountItem(
            membersCount = usersCount,
            onClick = onUsersClicked
        )
    }
}

@Composable
fun BottomActions(
    startBroadCast: () -> Unit,
    translationUiState: TranslationUiState,
    changeFacing: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CameraOrientationRow(
            onChange = changeFacing,
            selectedFacing = translationUiState.selectedCamera ?: Facing.FRONT
        )
        Spacer(modifier = Modifier.height(12.dp))
        GradientButton(
            text = stringResource(id = R.string.translations_start_strean),
            online = true,
            onClick = startBroadCast
        )
        Spacer(modifier = Modifier.height(53.dp))
    }
}

@Composable
fun BottomActionsExpired(
    onExtendCLicked: () -> Unit
) {
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