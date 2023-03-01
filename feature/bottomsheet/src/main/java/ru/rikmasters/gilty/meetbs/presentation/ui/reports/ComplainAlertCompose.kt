package ru.rikmasters.gilty.meetbs.presentation.ui.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ComplainAlertPreview() {
    GiltyTheme {
        ComplainAlert(true)
    }
}

@Composable
fun ComplainAlert(
    state: Boolean,
    onDismiss: (() -> Unit)? = null,
) {
    GAlert(
        show = state,
        modifier = Modifier,
        success = Pair(stringResource(R.string.meeting_close_button)) { onDismiss?.let { it() } },
        label = stringResource(R.string.chat_complaint_moderate),
        title = "${stringResource(R.string.complaints_send_answer)}?"
    )
}