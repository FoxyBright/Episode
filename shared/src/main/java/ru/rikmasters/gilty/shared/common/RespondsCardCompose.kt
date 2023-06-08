package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.shared.UserAvatar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun RespondCardPreview() {
    GiltyTheme { Responds(LastRespond.DemoLastRespond) }
}

@Preview
@Composable
private fun RespondCardEmptyPreview() {
    GiltyTheme { Responds(LastRespond.DemoLastRespond) }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Responds(
    lastRespond: LastRespond,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.profile_responds_label),
    onClick: (() -> Unit)? = null,
) {
    Card(
        { onClick?.let { it() } },
        modifier.fillMaxWidth(),
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
                .padding(vertical = 8.dp),
            SpaceBetween,
            CenterVertically,
        ) {
            val style = typography.labelSmall.copy(
                fontSize = 14.dp.toSp(),
                fontWeight = SemiBold,
            )
            Row(
                Modifier, Start,
                CenterVertically
            ) {
                lastRespond.image?.let {
                    UserAvatar(
                        image = it,
                        imageSize = 40,
                        group = lastRespond.group
                    )
                }
                Text(
                    text, Modifier
                        .padding(16.dp, 6.dp)
                        .padding(vertical = lastRespond.image?.let {
                            0.dp
                        } ?: 6.dp),
                    colorScheme.tertiary,
                    style = style
                )
            }
            Row(
                Modifier, Start,
                CenterVertically
            ) {
                lastRespond.count.let {
                    if (it > 0) Box(
                        Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(colorScheme.primary)
                    ) {
                        Text(
                            "$it", Modifier.padding(10.dp, 4.dp),
                            White, style = style
                        )
                    }
                }
                Icon(
                    Filled.KeyboardArrowRight,
                    (null), Modifier
                        .padding(start = 2.dp)
                        .size(28.dp),
                    colorScheme.onTertiary
                )
            }
        }
    }
}