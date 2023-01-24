package ru.rikmasters.gilty.mainscreen.presentation.ui.categories

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.enabled_check_box
import ru.rikmasters.gilty.shared.R.drawable.ic_back
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun CategoryListPreview() {
    GiltyTheme {
        CategoryList(
            CategoryListState(
                listOf(DemoCategoryModel),
                emptyList(), emptyList()
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
    val categoryList: List<CategoryModel>,
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
            Modifier
                .padding(bottom = 10.dp)
                .offset((-6).dp),
            Start, CenterVertically
        ) {
            IconButton(
                { callback?.onBack() },
            ) {
                Icon(
                    painterResource(ic_back),
                    (null), Modifier,
                    colorScheme.tertiary
                )
            }
            Text(
                stringResource(R.string.meeting_filter_select_categories),
                Modifier.padding(start = 14.dp),
                colorScheme.tertiary,
                style = typography.labelLarge
            )
        }
        Column(Modifier.fillMaxSize(), SpaceBetween) {
            LazyColumn(Modifier.weight(1f)) {
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
            }
            Buttons(
                Modifier, { callback?.onDone() })
            { callback?.onClear() }
        }
    }
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    Done: () -> Unit,
    Clear: () -> Unit
) {
    Column(modifier) {
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
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { Clear() },
            colorScheme.tertiary,
            textAlign = TextAlign.Center,
            style = typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(
    category: CategoryModel,
    select: Boolean,
    subCategories: List<Pair<CategoryModel, String>>,
    onSubSelect: (String) -> Unit,
    onClick: () -> Unit,
) {
    Column {
        Card(
            onClick, Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp), true,
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(colorScheme.primaryContainer)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                Arrangement.Absolute.SpaceBetween,
                CenterVertically
            ) {
                Row {
                    GEmojiImage(
                        category.emoji,
                        Modifier.size(20.dp)
                    )
                    Text(
                        category.name, Modifier
                            .padding(start = 18.dp),
                        colorScheme.tertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                }
                if(category.children != null) Icon(
                    if(select) Filled.KeyboardArrowDown
                    else Filled.KeyboardArrowRight,
                    stringResource(R.string.next_button),
                    tint = colorScheme.onTertiary
                ) else if(select)
                    Image(painterResource(enabled_check_box), (null))
            }
            category.children?.let { list ->
                if(select) (SubCategories(category, list, subCategories)
                { onSubSelect(it) })
            }
        }
    }
}

@Composable
private fun SubCategories(
    category: CategoryModel,
    subCategories: List<CategoryModel>,
    selectBubCategories:
    List<Pair<CategoryModel, String>>,
    onSelect: (String) -> Unit
) {
    Divider()
    FlowLayout(
        Modifier
            .background(colorScheme.primaryContainer)
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp), 8.dp, 8.dp
    ) {
        subCategories.forEach {
            GiltyChip(
                Modifier, it.name, selectBubCategories
                    .contains(Pair(category, it.name))
            ) { onSelect(it.name) }
        }
    }
}

