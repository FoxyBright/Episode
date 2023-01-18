package ru.rikmasters.gilty.profile.presentation.ui.lists.observers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.profile.presentation.ui.lists.observers.ObserveType.OBSERVED
import ru.rikmasters.gilty.profile.presentation.ui.lists.observers.ObserveType.OBSERVERS
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.digitalConverter
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelList
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.BrieflyRow
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GiltyTab
import ru.rikmasters.gilty.shared.shared.LazyItemsShapes
import ru.rikmasters.gilty.shared.shared.SmallButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun ObserversListPreview() {
    GiltyTheme {
        ObserversListContent(
            ObserversListState(
                DemoProfileModel,
                DemoMemberModelList, DemoMemberModelList,
                listOf(true, false)
            )
        )
    }
}

data class ObserversListState(
    val user: ProfileModel,
    val observers: List<MemberModel>,
    val observed: List<MemberModel>,
    val selectTab: List<Boolean>,
)

interface ObserversListCallback {
    fun onTabChange(point: Int) {}
    fun onDelete() {}
    fun onClick() {}
}

@Composable
fun ObserversListContent(
    state: ObserversListState,
    modifier: Modifier = Modifier,
    callback: ObserversListCallback? = null
) {
    Column(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        ActionBar(
            "${
                state.user.username
            }, ${state.user.age}",
            (null),
            Modifier.padding(bottom = 22.dp)
        )
        GiltyTab(
            listOf(
                "${stringResource(R.string.profile_observers)} ${
                    digitalConverter(state.observers.size)
                }", "${stringResource(R.string.profile_observe)} ${
                    digitalConverter(state.observed.size)
                }"
            ), state.selectTab,
            Modifier.padding(horizontal = 16.dp)
        ) { callback?.onTabChange(it) }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
                .background(
                    colorScheme.primaryContainer,
                    shapes.medium
                )
        ) {
            if (state.selectTab.first())
                itemsIndexed(state.observers) { index, it ->
                    Column {
                        ObserveItem(
                            it, OBSERVERS,
                            LazyItemsShapes(index, state.observers.size),
                            { callback?.onClick() }
                        ) { callback?.onDelete() }
                        if (index < state.observers.size - 1)
                            Divider(Modifier.padding(start = 16.dp))
                    }
                } else itemsIndexed(state.observed) { index, it ->
                Column {
                    ObserveItem(
                        it, OBSERVED,
                        LazyItemsShapes(index, state.observed.size),
                        { callback?.onClick() }
                    ) { callback?.onDelete() }
                    if (index < state.observed.size - 1)
                        Divider(Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

private enum class ObserveType { OBSERVERS, OBSERVED }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ObserveItem(
    member: MemberModel, type: ObserveType,
    shape: Shape, onItemClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        { onItemClick?.let { it() } },
        modifier.fillMaxWidth(), (true), shape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp),
            SpaceBetween, CenterVertically
        ) {
            BrieflyRow(
                member.avatar,
                "${
                    member.username
                }, ${
                    member.age
                }",
                member.emoji, Modifier
            )
            SmallButton(
                stringResource(
                    if (type == OBSERVERS) R.string.meeting_filter_delete_tag_label
                    else R.string.profile_user_observe
                ), colorScheme.outlineVariant,
                Color.White, Modifier
            ) { onDeleteClick?.let { it() } }
        }
    }
}