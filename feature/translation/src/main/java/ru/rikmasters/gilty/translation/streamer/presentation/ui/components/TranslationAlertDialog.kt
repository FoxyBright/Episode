package ru.rikmasters.gilty.translation.streamer.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GAlertDarkTheme

@Composable
fun TranslationStreamerDialog(
    type: TranslationDialogType,
    show: Boolean,
    onSuccess: () -> Unit,
    dismiss: () -> Unit
) {
    when (type) {
        TranslationDialogType.KICK -> {
            GAlertDarkTheme(
                show = show,
                success = Pair(
                    stringResource(id = R.string.translations_members_delete)
                ) {
                    onSuccess()
                },
                label = stringResource(id = R.string.translations_members_delete_label)
                ,
                title = stringResource(id = R.string.translations_members_delete_title),
                onDismissRequest = dismiss,
                cancel = Pair(stringResource(id = R.string.translations_complete_negative), dismiss)
            )
        }

        TranslationDialogType.COMPLAIN -> {
            GAlertDarkTheme(
                show = show,
                success = Pair(
                    stringResource(id = R.string.translations_viewer_complain_posiitive_nex)
                ) {
                    onSuccess()
                },
                label = stringResource(id = R.string.translations_viewer_complain_text),
                title = stringResource(id = R.string.translations_members_complain),
                onDismissRequest = dismiss,
                cancel = Pair(stringResource(id = R.string.translations_complete_negative), dismiss)
            )
        }

        TranslationDialogType.COMPLETE -> {
            GAlertDarkTheme(
                show = show,
                success = Pair(
                    stringResource(id = R.string.translations_complete_positive)
                ) {
                    onSuccess()
                },
                label = stringResource(id = R.string.translations_complete_text_b),
                title = stringResource(id = R.string.translations_complete),
                onDismissRequest = dismiss,
                cancel = Pair(stringResource(id = R.string.translations_complete_negative), dismiss)
            )
        }

        TranslationDialogType.COMPLETE_EARLIER -> {
            GAlertDarkTheme(
                show = show,
                success = Pair(
                    stringResource(id = R.string.translations_complete_positive)
                ) {
                    onSuccess()
                },
                label = stringResource(id = R.string.translations_complete_text),
                title = stringResource(id = R.string.translations_complete),
                onDismissRequest = dismiss,
                cancel = Pair(stringResource(id = R.string.translations_complete_negative), dismiss)
            )
        }
    }
}

enum class TranslationDialogType {
    KICK,
    COMPLAIN,
    COMPLETE,
    COMPLETE_EARLIER
}