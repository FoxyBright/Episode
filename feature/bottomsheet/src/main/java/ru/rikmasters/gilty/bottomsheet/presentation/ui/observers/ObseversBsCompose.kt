package ru.rikmasters.gilty.bottomsheet.presentation.ui.observers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.DELETE
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.SUB
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.UNSUB
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.digitalConverter
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModelList
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun ObserversListPreview() {
    GiltyTheme {
        ObserversListContent(
            ObserversListState(
                DemoProfileModel.username ?: "",
                pagingPreview(DemoUserModelList),
                pagingPreview(DemoUserModelList),
                emptyList(), (0), (""), (12 to 100)
            )
        )
    }
}


data class ObserversListState(
    val user: String,
    val observers: LazyPagingItems<UserModel>,
    val observed: LazyPagingItems<UserModel>,
    val unsubList: List<UserModel>,
    val selectTab: Int,
    val search: String,
    val counters: Pair<Int, Int>,
)

interface ObserversListCallback {
    
    fun onTabChange(point: Int)
    fun onButtonClick(member: UserModel, type: SubscribeType)
    fun onClick(member: UserModel)
    fun onSearchTextChange(text: String)
}

@Composable
fun ObserversListContent(
    state: ObserversListState,
    modifier: Modifier = Modifier,
    callback: ObserversListCallback? = null,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        ActionBar(state.user, Modifier.padding(bottom = 22.dp))
        GiltyTab(
            listOf(
                "${stringResource(R.string.profile_observers)} ${
                    digitalConverter(state.counters.first)
                }",
                "${stringResource(R.string.profile_observe)} ${
                    digitalConverter(state.counters.second)
                }"
            ),
            state.selectTab,
            Modifier.padding(horizontal = 16.dp)
        ) { callback?.onTabChange(it) }
        Search(
            state.search,
            Modifier.padding(16.dp, 18.dp)
        ) { callback?.onSearchTextChange(it) }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            list(
                if(state.selectTab == 0)
                    DELETE else SUB,
                state.unsubList,
                if(state.selectTab == 0)
                    state.observers
                else state.observed,
                callback
            )
        }
    }
}

private fun LazyListScope.list(
    subType: SubscribeType,
    unsubList: List<UserModel>,
    items: LazyPagingItems<UserModel>,
    callback: ObserversListCallback?,
) {
    val load = items.loadState
    when {
        load.refresh is LoadState.Error -> Unit
        load.append is LoadState.Error -> Unit
        else -> {
            if(load.refresh is LoadState.Loading)
                item { PagingLoader(load) }
            itemsIndexed(items) { index, member ->
                member?.let {
                    Box(
                        Modifier.background(
                            colorScheme.primaryContainer,
                            shapes.medium
                        )
                    ) {
                        when {
                            subType == DELETE -> subType
                            unsubList.contains(it) -> SUB
                            else -> UNSUB
                        }.let { type ->
                            ObserveItem(
                                Modifier, type, it,
                                index, items.itemCount,
                                { callback?.onClick(it) }
                            ) { callback?.onButtonClick(it, type) }
                        }
                    }
                }
            }
            if(load.append is LoadState.Loading)
                item { PagingLoader(load) }
            itemSpacer(40.dp)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ObserveItem(
    modifier: Modifier = Modifier,
    button: SubscribeType,
    member: UserModel,
    index: Int, size: Int,
    onItemClick: (() -> Unit)? = null,
    onButtonClick: (() -> Unit)? = null,
) {
    Column(modifier) {
        Card(
            { onItemClick?.let { it() } },
            Modifier.fillMaxWidth(),
            (true), lazyItemsShapes(index, size),
            cardColors(colorScheme.primaryContainer)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 12.dp),
                SpaceBetween, CenterVertically
            ) {
                BrieflyRow(
                    "${member.username}${
                        if(member.age in 18..99) {
                            ", ${member.age}"
                        } else ""
                    }",
                    Modifier.weight(1f),
                    member.avatar?.thumbnail?.url,
                    member.emoji
                )
                SmallButton(
                    stringResource(
                        when(button) {
                            SUB -> R.string.profile_organizer_observe
                            UNSUB -> R.string.profile_user_observe
                            DELETE -> R.string.meeting_filter_delete_tag_label
                        }
                    ),
                    color = if(button == SUB)
                        colorScheme.primary
                    else colorScheme.outlineVariant
                ) { onButtonClick?.let { it() } }
            }
        }
        if(index < size - 1) GDivider(
            Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun Search(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    val style = typography.bodyLarge.copy(
        colorScheme.tertiary
    )
    Box(
        modifier
            .fillMaxWidth()
            .background(
                colorScheme.primaryContainer,
                shapes.medium
            ),
        Center
    ) {
        Row(
            Modifier.padding(18.dp, 14.dp),
            Start,
            CenterVertically
        ) {
            Icon(
                painterResource(
                    R.drawable.magnifier
                ),
                (null),
                Modifier
                    .padding(end = 28.dp)
                    .size(20.dp),
                colorScheme.onTertiary
            )
            BasicTextField(
                value,
                { onValueChange(it) },
                Modifier
                    .fillMaxWidth()
                    .background(Transparent),
                textStyle = style,
                singleLine = true,
                maxLines = 1,
                cursorBrush = SolidColor(colorScheme.primary),
                keyboardOptions = Default.copy(
                    imeAction = Done,
                    keyboardType = Text,
                    capitalization = Sentences
                )
            ) {
                if(value.isEmpty()) Text(
                    stringResource(R.string.search_placeholder),
                    Modifier,
                    colorScheme.onTertiary,
                    style = typography.bodyLarge
                ); it()
            }
        }
    }
}
