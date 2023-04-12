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
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
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
import ru.rikmasters.gilty.shared.R.drawable.ic_back
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModelList
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun CategoryListPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            CategoryList(
                CategoryListState(
                    DemoCategoryModelList,
                    emptyList(), emptyList()
                ), Modifier.padding(16.dp)
            )
        }
    }
}

interface CategoryListCallback {
    
    fun onCategoryClick(index: Int, category: CategoryModel)
    fun onSubClick(category: CategoryModel)
    fun onBack()
    fun onComplete()
    fun onClear()
}

data class CategoryListState(
    val categories: List<CategoryModel>,
    val selected: List<CategoryModel>,
    val states: List<Int>,
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CategoryList(
    state: CategoryListState,
    modifier: Modifier = Modifier,
    callback: CategoryListCallback? = null,
) {
    Scaffold(modifier.padding(16.dp), {
        TopBar { callback?.onBack() }
    }, {
        Buttons(
            Modifier, { callback?.onComplete() })
        { callback?.onClear() }
    }) { Categories(Modifier.padding(it), state, callback) }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
) {
    Row(
        Modifier
            .padding(bottom = 10.dp)
            .offset((-6).dp),
        Start, CenterVertically
    ) {
        IconButton({ onBack() }) {
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
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Categories(
    modifier: Modifier = Modifier,
    state: CategoryListState,
    callback: CategoryListCallback? = null,
) {
    val states = state.states
    LazyColumn(modifier) {
        itemsIndexed(state.categories) { index, category ->
            Card(
                { callback?.onCategoryClick(index, category) },
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = shapes.large,
                colors = cardColors(colorScheme.primaryContainer),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    SpaceBetween, CenterVertically
                ) {
                    Row(Modifier, Start, CenterVertically) {
                        GEmojiImage(
                            category.emoji,
                            Modifier.size(20.dp)
                        )
                        Text(
                            category.name,
                            Modifier
                                .padding(start = 18.dp)
                                .padding(vertical = 16.dp),
                            colorScheme.tertiary,
                            style = typography.bodyMedium,
                            fontWeight = SemiBold
                        )
                    }
                    if(!category.children.isNullOrEmpty()) Icon(
                        if(states.contains(index))
                            Filled.KeyboardArrowDown
                        else Filled.KeyboardArrowRight, (null),
                        Modifier, colorScheme.onTertiary
                    )
                    else if(state.selected.contains(category)) Image(
                        painterResource(R.drawable.enabled_check_box),
                        (null), Modifier.size(32.dp)
                    )
                }
                if(!category.children.isNullOrEmpty()
                    && states.contains(index)
                ) {
                    GDivider(); FlowLayout(
                        Modifier
                            .background(colorScheme.primaryContainer)
                            .padding(top = 16.dp)
                            .padding(horizontal = 8.dp), 8.dp, 8.dp
                    ) {
                        category.children?.let {
                            it.forEach { sub ->
                                GChip(
                                    Modifier, sub.name,
                                    state.selected.contains(sub)
                                ) { callback?.onSubClick(sub) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit,
    onClear: () -> Unit,
) {
    Column(modifier) {
        GradientButton(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp),
            stringResource(R.string.meeting_filter_complete_button)
        ) { onComplete() }
        Text(
            stringResource(R.string.meeting_filter_clear),
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 28.dp)
                .clip(CircleShape)
                .clickable(
                    MutableInteractionSource(), (null)
                ) { onClear() },
            colorScheme.tertiary,
            textAlign = TextAlign.Center,
            style = typography.bodyLarge
        )
    }
}