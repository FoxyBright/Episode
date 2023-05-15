package ru.rikmasters.gilty.translation.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.rikmasters.gilty.translation.event.TranslationEvent
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.viewmodel.TranslationViewModel

@Composable
fun TestTranslationScreen(
    vm: TranslationViewModel,
    translationId: String
) {
    LaunchedEffect(Unit) {
        vm.onEvent(
            TranslationEvent.EnterScreen(
                translationId = translationId
            )
        )
    }

    val translationScreenState by vm.translationUiState.collectAsState()

    TranslationScreen(
        translationStatus = TranslationStatus.PREVIEW,
        onCloseClicked = { },
        translationUiState = translationScreenState,
        changeFacing = {
            vm.onEvent(TranslationEvent.ChangeFacing)
        }
    )
}