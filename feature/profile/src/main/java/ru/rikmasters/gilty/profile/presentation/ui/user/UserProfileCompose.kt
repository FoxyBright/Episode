package ru.rikmasters.gilty.profile.presentation.ui.user

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import kotlinx.coroutines.launch
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
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.USERPROFILE
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
            state = UserProfileState(
                profileState = ProfileState(
                    profile = DemoProfileModel,
                    profileType = USERPROFILE,
                ),
                currentMeetings = pagingPreview(
                    DemoMeetingList
                ),
                meetingsHistory = pagingPreview(
                    DemoMeetingList
                ),
                lastRespond = LastRespond(),
                historyState = (false),
                stateList = listOf(
                    INACTIVE, INACTIVE,
                    INACTIVE, INACTIVE, ACTIVE
                ),
                alert = false,
                listState = rememberLazyListState()
            ),
            modifier = Modifier
                .background(colorScheme.background)
        )
    }
}

data class UserProfileState(
    val profileState: ProfileState,
    val currentMeetings: LazyPagingItems<MeetingModel>,
    val meetingsHistory: LazyPagingItems<MeetingModel>,
    val lastRespond: LastRespond,
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
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavBar(state.stateList) {
                callback?.onNavBarSelect(it)
            }
        }
    ) {
        Profile(
            state = state,
            modifier = Modifier
                .padding(it),
            callback = callback
        )
    }
    ViewPhotography(state, callback)
    Alerts(state, callback)
}

@Composable
private fun ViewPhotography(
    state: UserProfileState,
    callback: UserProfileCallback?,
) = if(state.photoViewState) PhotoView(
    images = state.viewerImages,
    selected = state.viewerSelectImage,
    menuState = state.viewerMenuState,
    type = state.viewerType,
    onMenuClick = {
        callback?.onPhotoViewChangeMenuState(it)
    },
    onBack = {
        callback?.onPhotoViewDismiss(false)
    },
    menuItems = listOf(
        Pair(stringResource(R.string.edit_button)) {
            it?.let {
                callback?.profileImage(1)
            }
        },
    ),
) else Unit

@Composable
private fun ProfileTopBar(
    state: UserProfileState,
    callback: UserProfileCallback?,
) {
    Box(Modifier.padding(top = 10.dp)) {
        ProfileHeader(
            state = state.profileState,
            callback = callback
        )
        Box(
            Modifier.fillMaxWidth(),
            CenterEnd
        ) { MenuKebab(callback) }
    }
}

@Composable
private fun MenuKebab(
    callback: UserProfileCallback?,
) {
    IconButton(
        onClick = { callback?.menu(true) },
        modifier = Modifier.padding(top = 5.dp)
    ) {
        Icon(
            painter = painterResource(ic_kebab),
            contentDescription = null,
            tint = colorScheme.tertiary
        )
    }
}

@Composable
private fun Profile(
    state: UserProfileState,
    modifier: Modifier = Modifier,
    callback: UserProfileCallback?,
) {
    Box(modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ProfileTopBar(
                state = state,
                callback = callback
            )
            Use<UserProfileViewModel>(
                PullToRefreshTrait
            ) {
                Content(
                    state = state,
                    modifier = Modifier,
                    callback = callback
                )
            }
        }
    }
}

@Composable
private fun Alerts(
    state: UserProfileState,
    callback: UserProfileCallback?,
) {
    GAlert(
        show = state.alert,
        title = stringResource(
            R.string.complaints_send_answer
        ),
        onDismissRequest = {
            callback?.closeAlert()
        },
        label = stringResource(
            R.string.complaints_moderate_sen_answer
        ),
        success = Pair(
            stringResource(R.string.meeting_close_button)
        ) { callback?.closeAlert() }
    )
    GAlert(
        show = state.photoAlertState,
        onDismissRequest = {
            callback?.closePhotoAlert()
        },
        title = stringResource(
            R.string.profile_blocked_photo_alert
        ),
        success = Pair(
            stringResource(R.string.edit_button)
        ) { callback?.onMenuItemClick(1) },
        cancel = Pair(
            stringResource(R.string.notification_respond_cancel_button)
        ) { callback?.closePhotoAlert() }
    )
}

@Composable
private fun Content(
    state: UserProfileState,
    modifier: Modifier = Modifier,
    callback: UserProfileCallback? = null,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = state.listState
    ) {
        profile(state.profileState, callback)
        actualLabel()
        responds(state.lastRespond, callback)
        state.profileState.profile?.id?.let {
            actualList(
                userId = it,
                meetings = state.currentMeetings,
                callback = callback,
            )
            meetHistory(
                userId = it,
                historyState = state.historyState,
                historyList = state.meetingsHistory,
                callback = callback,
                listState = state.listState,
            )
        }
        itemSpacer(20.dp, key = 6)
    }
}

private fun LazyListScope.profile(
    profileState: ProfileState,
    callback: UserProfileCallback?,
    key: Any? = 1,
) = item(key) {
    Box(Modifier.padding(top = 10.dp)) {
        Profile(
            state = profileState,
            callback = callback,
            hasHeader = false
        )
    }
}

private fun LazyListScope.responds(
    lastRespond: LastRespond,
    callback: UserProfileCallback?,
    key: Any? = 3,
) = item(key) {
    Box(Modifier.padding(16.dp, 12.dp)) {
        Responds(lastRespond) {
            callback?.onRespondsClick()
        }
    }
}

private fun LazyListScope.actualLabel(
    key: Any? = 2,
) = item(key) {
    Text(
        text = stringResource(
            R.string.profile_actual_meetings_label
        ),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 16.dp),
        style = typography.labelLarge.copy(
            colorScheme.tertiary
        )
    )
}

private fun LazyListScope.meetHistory(
    userId: String,
    historyState: Boolean,
    historyList: LazyPagingItems<MeetingModel>,
    callback: UserProfileCallback?,
    listState: LazyListState,
    items: List<MeetingModel> =
        historyList.itemSnapshotList.items,
    key: Any? = 5,
) = if(items.isNotEmpty()) item(key) {
    val scope = rememberCoroutineScope()
    MeetHistory(
        userId = userId,
        historyState = historyState,
        historyList = historyList,
        openHistory = {
            scope.launch {
                listState.animateScrollBy(
                    value = 1000f,
                    animationSpec = tween(600)
                )
            }
            callback?.onHistoryShow()
        }
    ) { callback?.onHistoryClick(it) }
} else Unit

private fun LazyListScope.actualList(
    userId: String,
    meetings: LazyPagingItems<MeetingModel>,
    callback: UserProfileCallback?,
    key: Any? = 4,
    items: List<MeetingModel> =
        meetings.itemSnapshotList.items,
) = if(items.isNotEmpty()) item(key) {
    LazyRow(
        state = rememberLazyListScrollState(
            "profile_meet"
        )
    ) {
        itemSpacer(8.dp, true)
        items(meetings) {
            it?.let {
                MeetingCategoryCard(
                    userId = userId,
                    meeting = it,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                ) { callback?.onMeetingClick(it) }
            }
        }
    }
} else Unit

@Composable
private fun MeetHistory(
    userId: String,
    historyState: Boolean,
    historyList: LazyPagingItems<MeetingModel>,
    openHistory: () -> Unit,
    onSelect: (MeetingModel) -> Unit,
) {
    val openSize by animateDpAsState(
        targetValue = if(historyState)
            250.dp else 0.dp,
        animationSpec = tween(600)
    )
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 12.dp)
            .padding(horizontal = 16.dp)
            .clip(CircleShape)
            .clickable(
                MutableInteractionSource(),
                indication = null
            ) { openHistory() },
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
    LazyRow(
        modifier = Modifier.height(openSize),
        state = rememberLazyListScrollState(
            "profile_history_meet"
        )
    ) {
        itemSpacer(8.dp, true)
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