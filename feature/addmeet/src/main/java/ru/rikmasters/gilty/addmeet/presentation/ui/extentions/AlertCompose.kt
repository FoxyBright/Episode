package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GAlertDialog
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun AlertDialogPreview() {
    GiltyTheme { AlertDialog() }
}

@Composable
fun AlertDialog(
    positive: (() -> Unit)? = null,
    negative: (() -> Unit)? = null
) {
    GAlertDialog(
        stringResource(R.string.add_meet_exit_alert_title),
        Modifier.padding(40.dp),
        stringResource(R.string.add_meet_exit_alert_details),
        Pair(stringResource(R.string.cancel_button))
        { negative?.let { it() } },
        Pair(stringResource(R.string.save_button))
        { positive?.let { it() } }
    )
}