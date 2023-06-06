package ru.rikmasters.gilty.chat.presentation.ui.chat.popUp

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GPopUpMenu
import ru.rikmasters.gilty.shared.shared.METRICS

private val density = METRICS.density
private val displayWidth =
    METRICS.widthPixels / METRICS.density
private val displayHeight =
    METRICS.heightPixels / METRICS.density

@Composable
fun TopBarMenu(
    state: Boolean,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    GPopUpMenu(
        menuState = state,
        collapse = { onDismiss() },
        items = listOf(
            Triple(
                stringResource(R.string.exit_from_meet),
                colorScheme.tertiary
            ) { onDismiss(); onSelect(0) },
            Triple(
                stringResource(R.string.meeting_complain),
                colorScheme.tertiary
            ) { onDismiss(); onSelect(1) }
        ),
        modifier = Modifier.offset(
            x = (displayWidth / 3).dp,
            y = 50.dp
        )
    )
}

@Composable
fun BottomBarMenu(
    state: Boolean,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    GPopUpMenu(
        menuState = state,
        collapse = { onDismiss() },
        items = listOf(
            Triple(
                stringResource(R.string.chats_gallery_select_photo),
                colorScheme.tertiary
            ) { onDismiss();onSelect(0) },
            Triple(
                stringResource(R.string.chats_gallery_camera),
                colorScheme.tertiary
            ) { onDismiss(); onSelect(1) },
            Triple(
                stringResource(R.string.profile_hidden_photo),
                colorScheme.tertiary
            ) { onDismiss(); onSelect(2) }
        ),
        modifier =Modifier.offset(
            x = 30.dp,
            y = (displayHeight - 200).dp
        )
    )
}

@Composable
fun MessageMenu(
    state: Boolean,
    offset: Offset,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    GPopUpMenu(
        state, onDismiss, listOf(
            Triple(
                stringResource(R.string.answer_button),
                colorScheme.tertiary
            ) { onDismiss(); onSelect(0) },
            Triple(
                stringResource(R.string.meeting_filter_delete_tag_label),
                colorScheme.primary
            ) { onDismiss(); onSelect(1) }
        ), Modifier.offset(
            (offset.x / density).dp,
            (offset.y / density).dp
        )
    )
}
