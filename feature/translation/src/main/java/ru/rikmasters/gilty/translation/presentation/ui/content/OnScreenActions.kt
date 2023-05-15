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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCashedImage
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.model.TranslationUiState

@Composable
fun TopActions(
    translationStatus: TranslationStatus,
    onCloseClicked: () -> Unit,
    translationUiState: TranslationUiState
) {
    Column {
        if (translationStatus == TranslationStatus.PREVIEW) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .height(5.dp)
                    .width(40.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(11.dp),
                color = MaterialTheme.colorScheme.onTertiary
            ) {}
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.translations_preview_title),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineLarge
                )
                IconButton(onClick = onCloseClicked) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close preview"
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    translationUiState.meetingModel?.let {
                        GCashedImage(
                            url = it.organizer.thumbnail?.url,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(38.dp)
                                .clip(MaterialTheme.shapes.small)
                                    //TODO: test
                                .clickable { onCloseClicked() },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(11.dp))
                        Column {
                            Row {
                                Text(
                                    text = "${it.organizer.username}, ${it.organizer.age}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                it.organizer.emoji?.path?.toInt()?.let {
                                    Icon(
                                        painter = painterResource(id = it),
                                        contentDescription = "Emoji"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(0.5.dp))
                            Text(
                                text = it.category.name,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Row {
                    Surface(
                        shape = RoundedCornerShape(10.dp)
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
fun BottomActions(
    startBroadCast: () -> Unit,
    translationUiState: TranslationUiState,
    changeFacing: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier
                    .clickable {
                        changeFacing()
                    },
                color = if (translationUiState.selectedCamera == Facing.FRONT) {
                    MaterialTheme.colorScheme.scrim
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        vertical = 6.dp,
                        horizontal = 16.dp
                    ),
                    text = stringResource(id = R.string.translations_front_camera),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Surface(
                modifier = Modifier
                    .clickable {
                        changeFacing()
                    },
                color = if (translationUiState.selectedCamera == Facing.BACK) {
                    MaterialTheme.colorScheme.scrim
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        vertical = 6.dp,
                        horizontal = 16.dp
                    ),
                    text = stringResource(id = R.string.translations_main_camera),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier
                .clickable {
                    startBroadCast()
                }
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(Gradients.green()),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 16.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(Gradients.green())
                    ),
                text = stringResource(id = R.string.translations_start_strean),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(53.dp))
    }
}