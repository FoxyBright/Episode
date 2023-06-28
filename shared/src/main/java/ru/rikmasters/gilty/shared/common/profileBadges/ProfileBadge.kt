package ru.rikmasters.gilty.shared.common.profileBadges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ProfileBadgePreview() {
    GiltyTheme {
        Column(
            Modifier.background(
                MaterialTheme.colorScheme.background
            )
        ) {
            ProfileBadge(
                modifier = Modifier.padding(4.dp),
                group = PREMIUM
            )
            ProfileBadge(
                modifier = Modifier.padding(4.dp),
                group = TEAM
            )
            ProfileBadge(
                modifier = Modifier.padding(4.dp),
                group = ORIGINAL
            )
            ProfileBadge(
                modifier = Modifier.padding(4.dp),
                group = PARTNER
            )
            ProfileBadge(
                modifier = Modifier.padding(4.dp),
                group = DEFAULT
            )
        }
    }
}

@Composable
fun ProfileBadge(
    modifier: Modifier = Modifier,
    group: UserGroupTypeModel,
    labelSize: Int = 11,
    textPadding: PaddingValues =
        PaddingValues(10.dp, 4.dp),
    theme: Boolean =
        isSystemInDarkTheme(),
    shape: Shape =
        RoundedCornerShape(4.dp),
) {
    if(group == DEFAULT) return
    Box(
        modifier = modifier
            .background(
                color = group
                    .backColor(theme),
                shape = shape
            )
            .border(
                width = 1.dp,
                color = group
                    .borderColor(theme),
                shape = shape
            )
    ) {
        Text(
            text = group.name,
            modifier = Modifier
                .padding(textPadding),
            color = White,
            style = typography.labelSmall.copy(
                fontWeight = Bold,
                fontSize = labelSize.dp.toSp()
            ),
            maxLines = 1,
        )
    }
}

fun UserGroupTypeModel.backColor(
    isSystemInDarkTheme: Boolean,
) = if(isSystemInDarkTheme) when(this) {
    DEFAULT -> Transparent
    PREMIUM -> Color(0xFF474878)
    TEAM -> Color(0xFFB32B00)
    ORIGINAL -> Color(0xFF9C0D2F)
    PARTNER -> Color(0xFF12752C)
} else when(this) {
    DEFAULT -> Transparent
    PREMIUM -> Color(0xFF9091C1)
    TEAM -> Color(0xFFFC7449)
    ORIGINAL -> Color(0xFFE55678)
    PARTNER -> Color(0xFF5BBE75)
}

fun UserGroupTypeModel.borderColor(
    isSystemInDarkTheme: Boolean = false,
) = if(isSystemInDarkTheme) when(this) {
    DEFAULT -> Transparent
    PREMIUM -> Color(0xFF6667AB)
    TEAM -> Color(0xFFFF3D00)
    ORIGINAL -> Color(0xFFDF1343)
    PARTNER -> Color(0xFF1AA73E)
} else when(this) {
    DEFAULT -> Transparent
    PREMIUM -> Color(0xFF6667AB)
    TEAM -> Color(0xFFFF3D00)
    ORIGINAL -> Color(0xFFDF1343)
    PARTNER -> Color(0xFF1AA73E)
}