package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GAlert

@Composable
fun CloseAddMeetAlert(
    state: Boolean,
    cancel: () -> Unit,
    success: () -> Unit,
) {
    GAlert(
        state,
        Modifier,
        Pair(stringResource(R.string.exit_button), success),
        stringResource(R.string.add_meet_exit_alert_details),
        stringResource(R.string.add_meet_exit_alert_title),
        cancel, Pair(stringResource(R.string.cancel_button), cancel),
        accentColors = if(Online) colorScheme.secondary
        else colorScheme.primary
    )
}