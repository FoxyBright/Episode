package ru.rikmasters.gilty.bottomsheet.presentation.ui.organizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Profile
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.GDropMenu
import ru.rikmasters.gilty.shared.shared.GKebabButton
import ru.rikmasters.gilty.shared.shared.MeetingCategoryCard
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun OrganizerProfilePreview() {
    GiltyTheme {
        OrganizerContent(
            UserState(
                ProfileState(
                    DemoProfileModel,
                    ORGANIZER, (false)
                ), DemoMeetingList, (true)
            )
        )
    }
}

data class UserState(
    val profileState: ProfileState,
    val currentMeetings: List<MeetingModel>,
    val backButton: Boolean = false,
    val menuState: Boolean = false,
    val isMyProfile: Boolean = false,
)

interface OrganizerCallback: ProfileCallback {
    
    fun onMenuDismiss(state: Boolean)
    fun onMenuItemSelect(point: Int, userId: String?)
    fun onMeetingClick(meet: MeetingModel)
}

@Composable
fun OrganizerContent(
    state: UserState,
    modifier: Modifier = Modifier,
    callback: OrganizerCallback? = null,
) {
    val user = state.profileState.profile
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        item {
            TopBar(
                ("${user?.username}, ${user?.age}"),
                state.backButton, state.menuState,
                state.isMyProfile,
                { callback?.onBack() },
                { callback?.onMenuDismiss(it) },
            ) { callback?.onMenuItemSelect(it, user?.id) }
        }
        item {
            Profile(
                state.profileState,
                Modifier.padding(horizontal = 16.dp),
                callback
            ) { callback?.onObserveChange(it) }
        }
        if(state.currentMeetings.isNotEmpty()) {
            item { MeetLabel() }
            item {
                ActualMeetings(state.currentMeetings)
                { callback?.onMeetingClick(it) }
            }
        }
    }
}

@Composable
private fun MeetLabel() {
    Text(
        stringResource(R.string.profile_actual_meetings_label),
        Modifier
            .padding(top = 28.dp, bottom = 14.dp)
            .padding(horizontal = 16.dp),
        colorScheme.tertiary,
        style = typography.titleLarge
    )
}

@Composable
private fun ActualMeetings(
    meets: List<MeetingModel>,
    onMeetClick: (MeetingModel) -> Unit,
) {
    LazyRow {
        item { Spacer(Modifier.width(8.dp)) }
        items(meets) {
            MeetingCategoryCard(
                it, Modifier.padding(
                    horizontal = 4.dp
                )
            ) { onMeetClick(it) }
        }
    }
}

@Composable
private fun TopBar(
    username: String,
    backButton: Boolean,
    menuState: Boolean,
    isMyProfile: Boolean,
    onBack: () -> Unit,
    onKebabClick: (Boolean) -> Unit,
    onMenuItemSelect: (Int) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp, 18.dp),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            Start, CenterVertically
        ) {
            if(backButton) IconButton(
                { onBack() },
                Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    stringResource(R.string.action_bar_button_back),
                    Modifier, colorScheme.tertiary
                )
            }
            Text(
                username, Modifier,
                colorScheme.tertiary,
                style = typography.labelLarge,
                overflow = Ellipsis,
                maxLines = 1
            )
        }
        if(!isMyProfile) {
            GDropMenu(
                menuState,
                { onKebabClick(false) },
                menuItem = listOf(Pair(
                    stringResource(R.string.complaints_title)
                ) { onMenuItemSelect(0) })
            )
            GKebabButton { onKebabClick(true) }
        }
    }
}