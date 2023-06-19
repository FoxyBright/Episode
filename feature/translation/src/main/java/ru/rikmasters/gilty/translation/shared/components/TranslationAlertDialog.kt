package ru.rikmasters.gilty.translation.shared.components

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
                cancel = Pair(stringResource(id = R.string.translations_complete_negative), dismiss),
                onlyDarkTheme = true
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
                cancel = Pair(stringResource(id = R.string.translations_complete_negative), dismiss),
                onlyDarkTheme = true
            )
        }
        TranslationDialogType.EXIT -> {
            GAlertDarkTheme(
                show = show,
                success = Pair(stringResource(id = R.string.translations_viewer_complain_exit_positive)) {
                    onSuccess()
                },
                label = stringResource(id = R.string.translations_viewer_complain_exit_text),
                title = stringResource(id = R.string.translations_viewer_complain_exit),
                onDismissRequest = dismiss,
                cancel = Pair(stringResource(id = R.string.translations_complete_negative), dismiss),
                onlyDarkTheme = true
            )
        }
    }
}

enum class TranslationDialogType {
    KICK,
    COMPLAIN,
    EXIT
}