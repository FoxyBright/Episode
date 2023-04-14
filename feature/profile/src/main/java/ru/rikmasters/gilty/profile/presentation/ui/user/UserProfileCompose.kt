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
import androidx.compose.ui.Alignment
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
                    DemoProfileModel,
                    ProfileType.USERPROFILE
                ), pagingPreview(DemoMeetingList),
                pagingPreview(DemoMeetingList),
                Pair(4, "image"), (false), listOf(
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
    val lastRespond: Pair<Int, String?>,
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
            state.menuState,
            { callback?.onMenuClick(false) },
            DpOffset(180.dp, 300.dp), listOf(
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
        modifier,
        bottomBar = {
            NavBar(state.stateList) {
                callback?.onNavBarSelect(it)
            }
        }
    ) {
        Box(Modifier.padding(it)) {
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
        success = Pair(stringResource(R.string.edit_button)) {
            callback?.onMenuItemClick(
                1
            )
        },
        cancel = Pair(stringResource(R.string.notification_respond_cancel_button)) { callback?.closePhotoAlert() }
    )
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
                    Modifier,
                    callback
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
                            null,
                            Modifier,
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
            Box(Modifier.padding(16.dp, 12.dp)) {
                Responds(
                    state.lastRespond.second,
                    state.lastRespond.first
                ) { callback?.onRespondsClick() }
            }
        }
        val userId = state.profileState.profile?.id ?: ""
        if(state.currentMeetings.itemSnapshotList.items.isNotEmpty()) item {
            LazyRow {
                item { Spacer(Modifier.width(8.dp)) }
                items(state.currentMeetings) {
                    MeetingCategoryCard(
                        userId,
                        it!!,
                        Modifier.padding(horizontal = 4.dp)
                    ) { callback?.onMeetingClick(it) }
                }
            }
        }
        if(state.meetingsHistory.itemSnapshotList.items.isNotEmpty()) item(
            5
        ) {
            MeetHistory(
                userId,
                state.historyState,
                state.meetingsHistory,
                { callback?.onHistoryShow() }
            ) { callback?.onHistoryClick(it) }
        }
        item { Divider(Modifier.fillMaxWidth(), 20.dp, Transparent) }
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
        Start,
        CenterVertically
    ) {
        Text(
            stringResource(R.string.profile_meeting_history_label),
            Modifier.padding(end = 12.dp, bottom = 2.dp),
            color = colorScheme.tertiary,
            style = typography.labelLarge
        )
        Icon(
            if(!historyState) Filled.KeyboardArrowRight
            else Filled.KeyboardArrowDown,
            (null),
            Modifier.size(24.dp),
            colorScheme.tertiary
        )
    }
    if(historyState) LazyRow {
        item { Spacer(Modifier.width(8.dp)) }
        items(historyList) {
            MeetingCategoryCard(
                userId,
                it!!,
                Modifier.padding(horizontal = 4.dp),
                old = true
            ) { onSelect(it) }
        }
    }
}
