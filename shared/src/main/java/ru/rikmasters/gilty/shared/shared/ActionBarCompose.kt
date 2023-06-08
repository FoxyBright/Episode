package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private const val TITLE = "Заголовок"
private const val DETAILS = "Детали"
private const val DESCRIPTION = "Описание"

@Composable
fun ActionBar(
    title: String,
    modifier: Modifier = Modifier,
    details: String? = null,
    extra:String? = null,
    titleStyle: TextStyle = typography.titleLarge,
    detailedStyle: TextStyle = typography.labelSmall,
    extraStyle: TextStyle = typography.bodyMedium.copy(fontWeight = FontWeight(600)),
    onBack: (() -> Unit)? = null,
) {
    Column(modifier.fillMaxWidth()) {
        onBack?.let {
            IconButton(it, Modifier.padding(top = 16.dp)) {
                Icon(
                    painterResource(
                        R.drawable.ic_back
                    ),
                    stringResource(R.string.action_bar_button_back),
                    Modifier.size(24.dp),
                    colorScheme.tertiary
                )
            }
        }
        Row(Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.Bottom){
            Text(
                title,
                Modifier,
                style = titleStyle,
                color = colorScheme.tertiary
            )
            extra?.let {
                Text(
                    text = extra,
                    Modifier.padding(start = 4.dp, bottom = 3.dp),
                    colorScheme.onTertiary,
                    style = extraStyle
                )
            }
        }
        details?.let {
            Text(
                it, Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp),
                style = detailedStyle,
                color = colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun RowActionBar(
    title: String,
    modifier: Modifier = Modifier,
    details: String? = null,
    detailsColor: Color = colorScheme.primary,
    description: String? = null,
    distanceBetween: Dp? = null,
    onBack: (() -> Unit)? = null,
) {
    Column(modifier) {
        Row(verticalAlignment = CenterVertically) {
            onBack?.let {
                IconButton(
                    it, Modifier.padding(
                        end = distanceBetween ?: 16.dp
                    )
                ) {
                    Icon(
                        painterResource(R.drawable.ic_back),
                        stringResource(R.string.action_bar_button_back),
                        Modifier.size(24.dp),
                        colorScheme.tertiary
                    )
                }
            }
            Text(
                title,
                Modifier.padding(end = 8.dp),
                colorScheme.tertiary,
                style = typography.labelLarge
            )
            details?.let {
                Text(
                    details, style = typography.labelLarge,
                    color = detailsColor
                )
            }
        }
        description?.let {
            Text(
                it, Modifier.padding(start = 16.dp),
                colorScheme.onTertiary,
                style = typography.labelSmall
            )
        }
    }
}

@Composable
fun ClosableActionBar(
    title: String,
    modifier: Modifier = Modifier,
    details: String? = null,
    onClose: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            onBack?.let {
                IconButton(it) {
                    Icon(
                        painterResource(R.drawable.ic_back),
                        stringResource(R.string.action_bar_button_back),
                        Modifier.size(24.dp),
                        colorScheme.tertiary
                    )
                }
            }
            Image(
                painterResource(
                    R.drawable.ic_cross_button
                ), (null), Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .align(TopEnd)
                    .size(20.dp)
                    .clickable { onClose?.let { it() } },
                colorFilter = tint(colorScheme.scrim)
            )
        }
        Text(
            title, Modifier
                .padding(start = 16.dp)
                .fillMaxWidth(),
            style = typography.titleLarge.copy(
                lineHeight = 37.dp.toSp()
            ), color = colorScheme.tertiary
        )
        details?.let {
            Text(
                it, Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp),
                style = typography.labelSmall,
                color = colorScheme.onTertiary
            )
        }
    }
}

@Composable
@Preview
private fun ClosableActionBarPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            ClosableActionBar(
                TITLE, Modifier.padding(16.dp),
                DETAILS
            )
        }
    }
}

@Composable
@Preview
private fun ClosableActionBarWithBackPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            ClosableActionBar(
                TITLE, Modifier.padding(16.dp),
                DETAILS
            ) {}
        }
    }
}

@Composable
@Preview
private fun BackRowActionBarPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            RowActionBar(
                TITLE, Modifier.padding(16.dp),
                DETAILS, colorScheme.primary,
                DESCRIPTION
            ) {}
        }
    }
}

@Composable
@Preview
private fun RowActionBarPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            RowActionBar(
                TITLE, Modifier.padding(16.dp),
                DETAILS, colorScheme.primary,
                DESCRIPTION
            )
        }
    }
}

@Composable
@Preview
private fun ActionBarPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            ActionBar(
                TITLE, Modifier.padding(16.dp),
                DETAILS
            )
        }
    }
}

@Composable
@Preview
private fun ActionBarWithBackPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            ActionBar(
                TITLE, Modifier.padding(16.dp),
                DESCRIPTION
            ) {}
        }
    }
}