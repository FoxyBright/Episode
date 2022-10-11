package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.presentation.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.presentation.ui.shared.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.presentation.ui.shared.CategoryItem
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(showBackground = true)
@Composable
fun SendReactionBottomComposePreview() {
    GiltyTheme {
        SendReactionBottomCompose()
    }
}

@Composable
fun SendReactionBottomCompose(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp, 14.dp))
    ) {
        Column {
            Text(
                "Отклик отправлен",
                Modifier.padding(top = 16.dp, start = 16.dp),
                color = ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H2
            )
            Text(
                "Когда организатор примет ваш\nотклик, вам будет доступен чат\nвстречи",
                Modifier.padding(top = 8.dp, start = 16.dp, bottom = 80.dp),
                color = ThemeExtra.colors.secondaryTextColor,
                style = ThemeExtra.typography.SubHeadSb
            )
        }
        CategoryItem(
            DemoCategoryModel,
            true,
            Modifier
                .align(Alignment.TopEnd)
                .offset((CATEGORY_ELEMENT_SIZE / 6).dp, (-CATEGORY_ELEMENT_SIZE / 6).dp)
        )
    }
}
