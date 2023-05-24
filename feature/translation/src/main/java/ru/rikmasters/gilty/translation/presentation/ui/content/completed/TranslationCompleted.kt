package ru.rikmasters.gilty.translation.presentation.ui.content.completed

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun TranslationCompleted(
    thumbnailUrl: String?,
    onToChatPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = ThemeExtra.colors.preDarkColor
            )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = ThemeExtra.colors.preDarkColor
                )
        ) {
            thumbnailUrl?.let {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = "",
                    modifier = Modifier.background(
                        color = ThemeExtra.colors.preDarkColor
                    )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_admiration),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        text = stringResource(id = R.string.translations_completed),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeExtra.colors.white
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        text = stringResource(id = R.string.translations_completed_text),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        color = ThemeExtra.colors.white
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GradientButton(
                        text = stringResource(id = R.string.translations_expired_button_to_chat),
                        online = true,
                        onClick = onToChatPressed
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}