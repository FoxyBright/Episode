package ru.rikmasters.gilty.shared.common.profileBadges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
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
            ProfileBadge(modifier = Modifier.padding(4.dp), group = UserGroupTypeModel.PREMIUM)
            ProfileBadge(modifier = Modifier.padding(4.dp), group = UserGroupTypeModel.TEAM)
            ProfileBadge(modifier = Modifier.padding(4.dp), group = UserGroupTypeModel.ORIGINAL)
            ProfileBadge(modifier = Modifier.padding(4.dp), group = UserGroupTypeModel.PARTNER)
            ProfileBadge(modifier = Modifier.padding(4.dp), group = UserGroupTypeModel.DEFAULT)
        }
    }
}

@Composable
fun ProfileBadge(
    modifier: Modifier = Modifier,
    group: UserGroupTypeModel,
    labelSize:Int = 11,
    textPadding:PaddingValues = PaddingValues(vertical = 4.dp, horizontal = 10.dp)
) {
    if (group == UserGroupTypeModel.DEFAULT) return
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(group.getBackgroundColor(isSystemInDarkTheme()))
            .border(1.dp, group.getBorderColor(isSystemInDarkTheme()), RoundedCornerShape(4.dp))
    ) {
        Text(
            text = group.name,
            modifier = Modifier.padding(textPadding),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight(700), fontSize = labelSize.sp),
            maxLines = 1,
        )
    }
}

fun UserGroupTypeModel.getBackgroundColor(isSystemInDarkTheme: Boolean): Color =
    if (isSystemInDarkTheme) {
        when (this) {
            UserGroupTypeModel.DEFAULT -> Color.Transparent
            UserGroupTypeModel.PREMIUM -> Color(0xFF474878)
            UserGroupTypeModel.TEAM -> Color(0xFFB32B00)
            UserGroupTypeModel.ORIGINAL -> Color(0xFF9C0D2F)
            UserGroupTypeModel.PARTNER -> Color(0xFF12752C)
        }
    } else
        when (this) {
            UserGroupTypeModel.DEFAULT -> Color.Transparent
            UserGroupTypeModel.PREMIUM -> Color(0xFF9091C1)
            UserGroupTypeModel.TEAM -> Color(0xFFFC7449)
            UserGroupTypeModel.ORIGINAL -> Color(0xFFE55678)
            UserGroupTypeModel.PARTNER -> Color(0xFF5BBE75)
        }

fun UserGroupTypeModel.getBorderColor(isSystemInDarkTheme: Boolean = false): Color =
    if (isSystemInDarkTheme)
        when (this) {
            UserGroupTypeModel.DEFAULT -> Color.Transparent
            UserGroupTypeModel.PREMIUM -> Color(0xFF6667AB)
            UserGroupTypeModel.TEAM -> Color(0xFFFF3D00)
            UserGroupTypeModel.ORIGINAL -> Color(0xFFDF1343)
            UserGroupTypeModel.PARTNER -> Color(0xFF1AA73E)
        }
    else
        when (this) {
            UserGroupTypeModel.DEFAULT -> Color.Transparent
            UserGroupTypeModel.PREMIUM -> Color(0xFF6667AB)
            UserGroupTypeModel.TEAM -> Color(0xFFFF3D00)
            UserGroupTypeModel.ORIGINAL -> Color(0xFFDF1343)
            UserGroupTypeModel.PARTNER -> Color(0xFF1AA73E)
        }