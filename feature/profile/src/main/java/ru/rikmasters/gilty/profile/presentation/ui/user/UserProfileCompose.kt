package ru.rikmasters.gilty.profile.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.gallery.photoview.PhotoView
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTO
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_kebab
import ru.rikmasters.gilty.shared.common.*
import ru.rikmasters.gilty.shared.common.extentions.rememberLazyListScrollState
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ProfilePreview() {
    GiltyTheme {
        ProfileContent(
            UserProfileState(
                ProfileState(
                    profile = DemoProfileModel,
                    profileType = ProfileType.USERPROFILE,
                ), currentMeetings = pagingPreview(DemoMeetingList),
                meetingsHistory = pagingPreview(DemoMeetingList),
                lastRespond = LastRespond.DemoLastRespond, historyState = (false), stateList = listOf(
                    INACTIVE, INACTIVE,
                    INACTIVE, INACTIVE, ACTIVE
                ), alert = false,
                listState = rememberLazyListState()
            ), Modifier.background(colorScheme.background)
        )
    }
}

data class UserProfileState(
    val profileState: ProfileState,
    val currentMeetings: LazyPagingItems<MeetingModel>,
    val meetingsHistory: LazyPagingItems<MeetingModel>,
    val lastRespond:LastRespond,
    val historyState: Boolean = false,
    val stateList: List<NavIconState>,
    val alert: Boolean,
    val menuState: Boolean = false,
    val listState: LazyListState,
    val photoAlertState: Boolean = false,
    val photoViewState: Boolean = false,
    val viewerImages: List<AvatarModel?> = emptyList(),
    val viewerSelectImage: AvatarModel? = null,
    val viewerMenuState: Boolean = false,
    val viewerType: PhotoViewType = PHOTO,
    val smthError: Boolean = false,
)

interface UserProfileCallback: ProfileCallback {

    fun menu(state: Boolean)
    fun onHistoryShow()
    fun onMeetingClick(meet: MeetingModel)
    fun onHistoryClick(meet: MeetingModel)
    fun onRespondsClick()
    fun onNavBarSelect(point: Int)
    fun closeAlert()
    fun closePhotoAlert()
    fun updateProfile()
    fun onMenuClick(it: Boolean)
    fun onMenuItemClick(point: Int)
    fun onPhotoViewDismiss(state: Boolean)
    fun onPhotoViewChangeMenuState(state: Boolean) = Unit
    fun onPhotoViewMenuItemClick(imageId: String) = Unit
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
            menuState = state.menuState,
            collapse = { callback?.onMenuClick(false) },
            offset = DpOffset(180.dp, 300.dp),
            menuItem = listOf(
                Pair(stringResource(R.string.profile_menu_watch_photo_button)) {
                    callback?.onMenuItemClick(0)
                },
                Pair(stringResource(R.string.edit_button)) {
                    callback?.onMenuItemClick(1)
                }
            )
        )
    }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavBar(state.stateList) {
                callback?.onNavBarSelect(it)
            }
        }
    ) {
        if(state.smthError) ErrorInternetConnection {
            callback?.updateProfile()
        } else Box(Modifier.padding(it)) {
            Use<UserProfileViewModel>(PullToRefreshTrait) {
                Content(state, Modifier, callback)
            }
        }
    }
    if(state.photoViewState) PhotoView(
        images = state.viewerImages,
        selected = state.viewerSelectImage,
        menuState = state.viewerMenuState,
        type = state.viewerType,
        onMenuClick = { callback?.onPhotoViewChangeMenuState(it) },
        onMenuItemClick = { callback?.onPhotoViewMenuItemClick(it) },
        onBack = { callback?.onPhotoViewDismiss(false) },
    )
    GAlert(
        show = state.alert,
        title = stringResource(R.string.complaints_send_answer),
        onDismissRequest = { callback?.closeAlert() },
        label = stringResource(R.string.complaints_moderate_sen_answer),
        success = Pair(stringResource(R.string.meeting_close_button)) { callback?.closeAlert() }
    )
    GAlert(
        state.photoAlertState,
        onDismissRequest = { callback?.closePhotoAlert() },
        title = stringResource(R.string.profile_blocked_photo_alert),
        success = Pair(stringResource(R.string.edit_button))
        { callback?.onMenuItemClick(1) },
        cancel = Pair(stringResource(R.string.notification_respond_cancel_button))
        { callback?.closePhotoAlert() }
    )
}

@Composable
private fun Content(
    state: UserProfileState,
    modifier: Modifier = Modifier,
    callback: UserProfileCallback? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.padding(top = 10.dp)) {
            ProfileHeader(state = state.profileState, callback = callback)
            Box(
                Modifier.fillMaxWidth(),
                CenterEnd
            ) {
                IconButton(
                    onClick = { callback?.menu(true) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(ic_kebab),
                        contentDescription = null,
                        tint = colorScheme.tertiary
                    )
                }
            }
        }
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            state = state.listState
        ) {
            item(key = 1) {
                Box(Modifier.padding(top = 10.dp)) {
                    Profile(
                        state = state.profileState,
                        callback = callback,
                        hasHeader = false
                    )
                }
            }
            item(key = 2) {
                Text(
                    text = stringResource(R.string.profile_actual_meetings_label),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(horizontal = 16.dp),
                    style = typography.labelLarge.copy(
                        colorScheme.tertiary
                    )
                )
            }
            item(key = 3) {
                Box(Modifier.padding(16.dp, 12.dp)) {
                    Responds(
                        state.lastRespond
                    ) { callback?.onRespondsClick() }
                }
            }
            val userId = state.profileState.profile?.id ?: ""
            if (state.currentMeetings.itemSnapshotList.items.isNotEmpty()) item(
                key = 4
            ) {
                LazyRow(
                    state = rememberLazyListScrollState(
                        "profile_meet"
                    )
                ) {
                    item { Spacer(Modifier.width(8.dp)) }
                    items(state.currentMeetings) {
                        MeetingCategoryCard(
                            userId = userId,
                            meeting = it!!,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                        ) { callback?.onMeetingClick(it) }
                    }
                }
            }
            if (state.meetingsHistory.itemSnapshotList.items.isNotEmpty())
                item(5) {
                    MeetHistory(
                        userId = userId,
                        historyState = state.historyState,
                        historyList = state.meetingsHistory,
                        openHistory = { callback?.onHistoryShow() }
                    ) { callback?.onHistoryClick(it) }
                }
            item(key = 6) {
                Divider(
                    Modifier.fillMaxWidth(),
                    20.dp, Transparent
                )
            }
        }
    }
}

@Composable
private fun MeetHistory(
    userId: String,
    historyState: Boolean,
    historyList: LazyPagingItems<MeetingModel>,
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
            text = stringResource(R.string.profile_meeting_history_label),
            modifier = Modifier.padding(
                end = 12.dp, bottom = 2.dp
            ),
            color = colorScheme.tertiary,
            style = typography.labelLarge
        )
        Icon(
            imageVector = if(!historyState)
                Filled.KeyboardArrowRight
            else Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = colorScheme.tertiary
        )
    }
    if(historyState) LazyRow(
        state = rememberLazyListScrollState(
            "profile_history_meet"
        )
    ) {
        item { Spacer(Modifier.width(8.dp)) }
        items(historyList) {
            MeetingCategoryCard(
                userId = userId,
                meeting = it!!,
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                old = true
            ) { onSelect(it) }
        }
    }
}
