package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.CategoryModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

const val CATEGORY_ELEMENT_SIZE = 120

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true)
private fun CategoryItemTopPreview() {
    GiltyTheme {
        CategoryItem(
            DemoCategoryModel,
            CategoryItemState(itemState = true, false)
        )
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true)
private fun CategoryItemBottomPreview() {
    GiltyTheme {
        CategoryItem(DemoCategoryModel, CategoryItemState(false))
    }
}

interface CategoryItemCallback : NavigationInterface {
    fun onCategoryClick(category: CategoryModel)
}

data class CategoryItemState(
    val itemState: Boolean,
    val categoryIconBottomState: Boolean = true
)

@Composable
fun CategoryItem(
    item: CategoryModel,
    state: CategoryItemState,
    modifier: Modifier = Modifier,
    callback: CategoryItemCallback? = null
) {
    var iconState by remember { mutableStateOf(state.itemState) }
    Box(
        modifier.size((CATEGORY_ELEMENT_SIZE + 10).dp)
    ) {
        Box(
            Modifier
                .size(CATEGORY_ELEMENT_SIZE.dp)
                .clip(CircleShape)
                .background(
                    if (iconState) Color(item.color.toColorInt())
                    else ThemeExtra.colors.grayIcon
                )
                .align(Alignment.BottomCenter)
                .clickable {
                    iconState = !iconState
                    callback?.onCategoryClick(item)
                },
            Alignment.Center
        ) {
            Text(
                item.name,
                Modifier,
                Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = ThemeExtra.typography.MediumText
            )
        }
        Box(
            Modifier
                .size((CATEGORY_ELEMENT_SIZE / 3).dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(
                    if (state.categoryIconBottomState) Alignment.BottomStart
                    else Alignment.TopEnd
                ),
            Alignment.Center
        ) {
            AsyncImage(
                item.emoji.path,
                stringResource(R.string.next_button),
                Modifier.size((CATEGORY_ELEMENT_SIZE / 6).dp),
                painterResource(R.drawable.cinema)
            )
        }
    }
}