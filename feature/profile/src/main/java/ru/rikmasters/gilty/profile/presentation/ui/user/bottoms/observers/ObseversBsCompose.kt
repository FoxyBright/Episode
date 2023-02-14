package ru.rikmasters.gilty.profile.presentation.ui.user.bottoms.observers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel.SubscribeType
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel.SubscribeType.DELETE
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel.SubscribeType.SUB
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel.SubscribeType.UNSUB
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.digitalConverter
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
                DemoUserModelList, DemoUserModelList,
                emptyList(), (0)
            )
        )
    }
}

data class ObserversListState(
    val user: String,
    val observers: List<UserModel>,
    val observed: List<UserModel>,
    val unsubList: List<UserModel>,
    val selectTab: Int,
)

interface ObserversListCallback {
    
    fun onTabChange(point: Int) {}
    fun onButtonClick(member: UserModel, type: SubscribeType) {}
    fun onClick(member: UserModel) {}
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
            if(state.selectTab == 0)
                itemsIndexed(state.observers) { index, member ->
                    Column {
                        ObserveItem(
                            Modifier, DELETE, member,
                            lazyItemsShapes(index, state.observers.size),
                            { callback?.onClick(member) }
                        ) { callback?.onButtonClick(member, DELETE) }
                        if(index < state.observers.size - 1)
                            Divider(Modifier.padding(start = 16.dp))
                    }
                } else itemsIndexed(state.observed) { index, member ->
                Column {
                    val subType =
                        if(state.unsubList.contains(member)) SUB else UNSUB
                    ObserveItem(
                        Modifier, subType, member,
                        lazyItemsShapes(index, state.observed.size),
                        { callback?.onClick(member) }
                    ) { callback?.onButtonClick(member, subType) }
                    if(index < state.observed.size - 1)
                        Divider(Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ObserveItem(
    modifier: Modifier = Modifier,
    button: SubscribeType,
    member: UserModel,
    shape: Shape, onItemClick: (() -> Unit)? = null,
    onButtonClick: (() -> Unit)? = null,
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
                "${member.username}, ${member.age}",
                Modifier.weight(1f), member.avatar,
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
                color = if(button == SUB) colorScheme.primary
                else colorScheme.outlineVariant,
            ) { onButtonClick?.let { it() } }
        }
    }
}