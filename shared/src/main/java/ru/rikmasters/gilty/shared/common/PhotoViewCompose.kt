package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_back
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.shared.GDropMenu
import ru.rikmasters.gilty.shared.shared.GKebabButton
import ru.rikmasters.gilty.shared.shared.METRICS
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun PhotoPreview() {
    GiltyTheme {
        PhotoView(DemoMemberModel.avatar.id,
            false,
            Modifier, {}, {}) {}
    }
}

//enum class PhotoViewType { IMAGE, HIDDEN }

@Composable
fun PhotoView(
    image: String,
    menuState: Boolean,
    modifier: Modifier = Modifier,
    onMenuClick: ((Boolean) -> Unit)? = null,
    onMenuItemClick: ((Int) -> Unit)? = null,
    onBack: () -> Unit
) {
    Column(modifier.background(colorScheme.background)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorScheme.primaryContainer),
            SpaceBetween, CenterVertically
        ) {
            Row(Modifier, Start, CenterVertically) {
                IconButton(onBack, Modifier.padding(16.dp)) {
                    Icon(
                        painterResource(ic_back),
                        (null), Modifier.size(24.dp)
                    )
                }
                Text(
                    "1/1",
                    Modifier.padding(),
                    colorScheme.tertiary,
                    style = typography.headlineLarge
                ) // TODO временная заглушка - тут количество фотографий
            }
            onMenuClick?.let {
                GKebabButton(Modifier.padding(16.dp))
                { it(true) }
            }
        }
        AsyncImage(
            image, (null), Modifier
                .fillMaxSize()
                .weight(1f),
            contentScale = Fit
        )
    }
    GDropMenu(
        menuState, { onMenuClick?.let { it(false) } },
        DpOffset(
            ((METRICS.widthPixels / METRICS.density) - 160).dp, -(100).dp
        ), listOf(
            Pair(stringResource(R.string.edit_button))
            { onMenuItemClick?.let { it(0) } },
        )
    )
}