package ru.rikmasters.gilty.profile.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import ru.rikmasters.gilty.core.app.ui.ErrorConnection
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_kebab
import ru.rikmasters.gilty.shared.common.Profile
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.USERPROFILE
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ProfilePreview() {
    GiltyTheme {
        val meets = DemoMeetingList
        ProfileContent(
            UserProfileState(
                ProfileState(
                    DemoProfileModel, USERPROFILE
                ), meets, meets, Pair(4, "image"),
                (false), listOf(
                    INACTIVE, NEW, INACTIVE,
                    INACTIVE, ACTIVE
                ), alert = false,
                listState = rememberLazyListState()
            ), Modifier.background(colorScheme.background)
        )
    }
}

data class UserProfileState(
    val profileState: ProfileState,
    val currentMeetings: List<MeetingModel>,
    val meetingsHistory: List<MeetingModel>,
    val lastRespond: Pair<Int, String>,
    val historyState: Boolean = false,
    val stateList: List<NavIconState>,
    val alert: Boolean,
    val menuState: Boolean = false,
    val listState: LazyListState,
    val errorState: Boolean = false,
    val photoAlertState: Boolean = false,
)

interface UserProfileCallback: ProfileCallback {
    
    fun menu(state: Boolean) {}
    fun onHistoryShow() {}
    fun onMeetingClick(meet: MeetingModel) {}
    fun onHistoryClick(meet: MeetingModel) {}
    fun onRespondsClick() {}
    fun onNavBarSelect(point: Int) {}
    fun closeAlert() {}
    fun closePhotoAlert() {}
    fun onMenuClick(it: Boolean) {}
    fun onMenuItemClick(point: Int) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state: UserProfileState,
    modifier: Modifier = Modifier,
    callback: UserProfileCallback? = null,
) {
    Box {
        GDropMenu(
            state.menuState,
            { callback?.onMenuClick(false) },
            DpOffset(180.dp, 300.dp),
            listOf(
                Pair(stringResource(R.string.profile_menu_watch_photo_button))
                { callback?.onMenuItemClick(0) },
                Pair(stringResource(R.string.edit_button))
                { callback?.onMenuItemClick(1) },
            )
        )
    }
    Scaffold(
        modifier, bottomBar = {
            NavBar(state.stateList)
            { callback?.onNavBarSelect(it) }
        }
    ) {
        Box(Modifier.padding(it)) {
            Use<UserProfileViewModel>(PullToRefreshTrait) {
                Content(state, Modifier, callback)
            }
        }
    }
    GAlert(
        state.alert,
        title = stringResource(R.string.complaints_send_answer),
        onDismissRequest = { callback?.closeAlert() },
        label = stringResource(R.string.complaints_moderate_sen_answer),
        success = Pair(stringResource(R.string.meeting_close_button)) { callback?.closeAlert() }
    )
    GAlert(
        state.photoAlertState,
        onDismissRequest = { callback?.closePhotoAlert() },
        title = stringResource(R.string.profile_blocked_photo_alert),
        success = Pair(stringResource(R.string.edit_button)) { callback?.onMenuItemClick(1) },
        cancel = Pair(stringResource(R.string.notification_respond_cancel_button)) { callback?.closePhotoAlert() }
    )
    if(state.errorState) ErrorConnection()
}

@Composable
private fun Content(
    state: UserProfileState,
    modifier: Modifier = Modifier,
    callback: UserProfileCallback? = null,
) {
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(colorScheme.background),
        state.listState
    ) {
        item {
            Box(
                Modifier
                    .padding(top = 80.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Profile(
                    state.profileState,
                    Modifier, callback
                )
                Box(
                    Modifier.fillMaxWidth(),
                    Alignment.CenterEnd
                ) {
                    IconButton(
                        { callback?.menu(true) },
                        Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            painterResource(ic_kebab),
                            null, Modifier,
                            colorScheme.tertiary
                        )
                    }
                }
            }
        }
        item {
            Text(
                stringResource(R.string.profile_actual_meetings_label),
                Modifier
                    .padding(top = 28.dp)
                    .padding(horizontal = 16.dp),
                colorScheme.tertiary,
                style = typography.labelLarge
            )
        }
        item {
            Responds(
                state.lastRespond,
                Modifier.padding(16.dp, 12.dp)
            ) { callback?.onRespondsClick() }
        }
        if(state.currentMeetings.isNotEmpty()) item {
            LazyRow {
                item { Spacer(Modifier.width(8.dp)) }
                items(state.currentMeetings) {
                    MeetingCategoryCard(
                        it, Modifier.padding(horizontal = 4.dp)
                    ) { callback?.onMeetingClick(it) }
                }
            }
        }
        if(state.meetingsHistory.isNotEmpty()) item(5) {
            MeetHistory(state.historyState, state.meetingsHistory,
                { callback?.onHistoryShow() })
            { callback?.onHistoryClick(it) }
        }
        item { Divider(Modifier.fillMaxWidth(), 20.dp, Transparent) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Responds(
    last: Pair<Int, String>,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val style = typography.labelSmall
        .copy(fontWeight = SemiBold)
    Card(
        onClick, modifier.fillMaxWidth(),
        colors = cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 20.dp)
                .padding(vertical = 8.dp),
            SpaceBetween,
            CenterVertically,
        ) {
            Row(verticalAlignment = CenterVertically) {
                if(last.second.isNotBlank()) AsyncImage(
                    last.second, (null), Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .padding(end = 16.dp),
                    contentScale = Crop
                )
                Text(
                    stringResource(R.string.profile_responds_label),
                    Modifier.padding(vertical = 12.dp),
                    colorScheme.tertiary,
                    style = style
                )
            }
            Row(verticalAlignment = CenterVertically) {
                if(last.first > 0) Box(
                    Modifier
                        .clip(shapes.extraSmall)
                        .background(colorScheme.primary)
                ) {
                    Text(
                        last.first.toString(),
                        Modifier.padding(10.dp, 4.dp),
                        White, style = style
                    )
                }
                Icon(
                    Filled.KeyboardArrowRight,
                    stringResource(R.string.profile_responds_label),
                    Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
private fun MeetHistory(
    historyState: Boolean,
    historyList: List<MeetingModel>,
    openHistory: () -> Unit,
    onSelect: (MeetingModel) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 12.dp)
            .padding(horizontal = 16.dp)
            .clip(CircleShape)
            .clickable { openHistory() },
        Start, CenterVertically
    ) {
        Text(
            stringResource(R.string.profile_meeting_history_label),
            Modifier.padding(end = 12.dp, bottom = 2.dp),
            color = colorScheme.tertiary,
            style = typography.labelLarge
        )
        Icon(
            if(!historyState) Filled.KeyboardArrowRight
            else Filled.KeyboardArrowDown, (null),
            Modifier.size(24.dp), colorScheme.tertiary
        )
    }
    if(historyState) LazyRow {
        item { Spacer(Modifier.width(8.dp)) }
        items(historyList) {
            MeetingCategoryCard(
                it, Modifier.padding(horizontal = 4.dp),
                old = true
            ) { onSelect(it) }
        }
    }
}