package ru.rikmasters.gilty.chat.presentation.ui.chat.bottom

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_camera
import ru.rikmasters.gilty.shared.shared.EmptyScreen

@Composable
fun HiddenPhotoBottomSheet(modifier: Modifier = Modifier) {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(16.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            stringResource(R.string.profile_hidden_photo),
            modifier.align(Alignment.TopStart), colorScheme.tertiary,
            style = typography.labelLarge
        )
        EmptyScreen(
            stringResource(R.string.chats_empty_hidden_photos),
            ic_camera, Modifier, 50.dp, 16.dp
        )
    }
}