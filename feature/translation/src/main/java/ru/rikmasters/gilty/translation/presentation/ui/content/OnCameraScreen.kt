package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.model.TranslationUiState

@Composable
fun OnScreenContent(
    translationStatus: TranslationStatus,
    translationUiState: TranslationUiState,
    startBroadCast: () -> Unit,
    onCloseClicked: () -> Unit,
    changeFacing: () -> Unit,
    remainTime: String,
    userCount: Int,
    onChatClicked: () -> Unit,
    onUsersClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopActions(
                translationStatus = translationStatus,
                onCloseClicked = onCloseClicked,
                translationUiState = translationUiState,
                remainTime = remainTime
            )
            Spacer(modifier = Modifier.height(30.dp))
            MiddleActions(
                usersCount = userCount,
                onChatCLicked = onChatClicked,
                onUsersClicked = onUsersClicked
            )
        }

        if (translationStatus == TranslationStatus.PREVIEW) {
            BottomActions(
                startBroadCast = startBroadCast,
                translationUiState = translationUiState,
                changeFacing = changeFacing
            )
        }
    }
}