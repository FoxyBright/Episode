package ru.rikmasters.gilty.mainscreen.presentation.ui.categories

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoFullCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.FullCategoryModel
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun CategoryListPreview() {
    GiltyTheme {
        CategoryList(
            CategoryListState(
                DemoFullCategoryModelList,
                listOf(), listOf()
            ), Modifier.padding(16.dp)
        )
    }
}

interface CategoryListCallback {
    fun onCategoryClick(index: Int, it: Boolean)
    fun onSubSelect(category: CategoryModel, sub: String?) {}
    fun onBack() {}
    fun onDone() {}
    fun onClear() {}
}

data class CategoryListState(
    val categoryList: List<FullCategoryModel>,
    val categoryListState: List<Boolean>,
    val subCategories: List<Pair<CategoryModel, String>>
)

@Composable
fun CategoryList(
    state: CategoryListState,
    modifier: Modifier = Modifier,
    callback: CategoryListCallback? = null
) {
    Column(modifier) {
        Row(
            Modifier.padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_back),
                stringResource(R.string.action_bar_button_back),
                Modifier
                    .clip(CircleShape)
                    .clickable { callback?.onBack() },
                MaterialTheme.colorScheme.tertiary
            )
            Text(
                stringResource(R.string.meeting_filter_select_categories),
                Modifier.padding(start = 20.dp),
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyColumn {
            itemsIndexed(state.categoryList)
            { index, category ->
                val select = state.categoryListState[index]
                Item(category, select, state.subCategories,
                    { callback?.onSubSelect(category, it) })
                {
                    callback?.onSubSelect(category, null)
                    callback?.onCategoryClick(index, select)
                }
            }
            item {
                Buttons({ callback?.onDone() },
                    { callback?.onClear() })
            }
        }
    }
}

@Composable
private fun Buttons(Done: () -> Unit, Clear: () -> Unit) {
    Column {
        GradientButton(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp),
            stringResource(R.string.meeting_filter_complete_button)
        ) { Done() }
        Text(
            stringResource(R.string.meeting_filter_clear),
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 28.dp)
                .clip(CircleShape)
                .clickable { Clear() },
            MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(
    category: FullCategoryModel,
    select: Boolean,
    subCategories: List<Pair<CategoryModel, String>>,
    onSubSelect: (String) -> Unit,
    onClick: () -> Unit,
) {
    val subs = category.subcategories
    Column {
        Card(
            onClick, Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp), true,
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                Arrangement.Absolute.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Row {
                    AsyncImage(
                        category.emoji.path,
                        null,
                        Modifier.size(20.dp),
                        placeholder = painterResource(R.drawable.cinema)
                    )
                    Text(
                        category.name,
                        Modifier.padding(start = 18.dp),
                        MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (subs != null) Icon(
                    if (select) Icons.Filled.KeyboardArrowDown
                    else Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.next_button),
                    tint = MaterialTheme.colorScheme.onTertiary
                ) else if (select)
                    Image(painterResource(R.drawable.enabled_check_box), (null))
            }
            subs?.let { list ->
                if (select) (SubCategories(category, list, subCategories)
                { onSubSelect(it) })
            }
        }
    }
}

@Composable
private fun SubCategories(
    category: CategoryModel,
    subCategories: List<String>,
    selectBubCategories: List<Pair<CategoryModel, String>>,
    onSelect: (String) -> Unit
) {
    Divider()
    FlowLayout(
        Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp), 8.dp, 8.dp
    ) {
        subCategories.forEach {
            GiltyChip(
                Modifier, it,
                selectBubCategories.contains(Pair(category, it))
            ) { onSelect(it) }
        }
    }
}

