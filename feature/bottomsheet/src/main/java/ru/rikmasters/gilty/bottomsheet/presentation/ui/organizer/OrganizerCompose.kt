package ru.rikmasters.gilty.bottomsheet.presentation.ui.organizer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ru.rikmasters.gilty.gallery.photoview.PhotoView
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Profile
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.marquee
import ru.rikmasters.gilty.shared.common.profileBadges.ProfileBadge
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel.DEFAULT
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun OrganizerProfilePreview() {
    GiltyTheme {
        OrganizerContent(
            UserState(
                ProfileState(
                    profile = DemoProfileModel,
                    profileType = ORGANIZER,
                    observeState = (false),
                ), currentMeetings = DemoMeetingList, backButton = (true)
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
    val photoViewState: Boolean = false,
    val viewerImages: List<AvatarModel?> = emptyList(),
    val viewerSelectImage: AvatarModel? = null,
    val viewerMenuState: Boolean = false,
    val hiddenPhotoViewState: Boolean = false,
    val hiddenViewerImages: List<AvatarModel?> = emptyList(),
    val hiddenViewerSelectImage: AvatarModel? = null,
    val hiddenViewerMenuState: Boolean = false,
)

interface OrganizerCallback : ProfileCallback {

    fun onMenuDismiss(state: Boolean)
    fun onMenuItemSelect(point: Int, userId: String?)
    fun onMeetingClick(meet: MeetingModel)
    fun onPhotoViewDismiss(state: Boolean)
    fun onPhotoViewChangeMenuState(state: Boolean) = Unit
    fun onPhotoViewMenuItemClick(imageId: String) = Unit
}

@Composable
fun OrganizerContent(
    state: UserState,
    modifier: Modifier = Modifier,
    callback: OrganizerCallback? = null,
) {
    val user = state.profileState.profile
    Column(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        TopBar(
            username = "${user?.username}${
                if (user?.age in 18..99) {
                    ", ${user?.age}"
                } else ""
            }",
            backButton = state.backButton,
            menuState = state.menuState,
            isMyProfile = state.isMyProfile,
            profileGroup = state.profileState
                .profile?.group ?: DEFAULT,
            onBack = { callback?.onBack() },
            onKebabClick = {
                callback?.onMenuDismiss(it)
            },
        ) {
            callback?.onMenuItemSelect(
                point = it,
                userId = user?.id
            )
        }
        LazyColumn(modifier.fillMaxWidth()) {
            itemSpacer(18.dp)
            item {
                Profile(
                    state = state.profileState,
                    callback = callback,
                    isMyProfile = state.isMyProfile
                ) { callback?.onObserveChange(it) }
            }
            if (state.currentMeetings.isNotEmpty()) {
                item { MeetLabel() }
                item {
                    ActualMeetings(state.currentMeetings)
                    { callback?.onMeetingClick(it) }
                }
                itemSpacer(40.dp)
            }
        }
        if (state.hiddenPhotoViewState) PhotoView(
            images = state.hiddenViewerImages,
            selected = state.hiddenViewerSelectImage,
            menuState = state.hiddenViewerMenuState,
            type = PhotoViewType.LOAD,
            onMenuClick = { callback?.onPhotoViewChangeMenuState(it) },
            onMenuItemClick = { callback?.onPhotoViewMenuItemClick(it) },
            onBack = { callback?.onPhotoViewDismiss(false) },
        )
        if (state.photoViewState) PhotoView(
            images = state.viewerImages,
            selected = state.viewerSelectImage,
            menuState = state.viewerMenuState,
            type = PhotoViewType.PHOTO,
            onMenuClick = { callback?.onPhotoViewChangeMenuState(it) },
            onMenuItemClick = { callback?.onPhotoViewMenuItemClick(it) },
            onBack = { callback?.onPhotoViewDismiss(false) },
        )
    }
}

@Composable
private fun MeetLabel() {
    Text(
        text = stringResource(R.string.profile_actual_meetings_label),
        modifier = Modifier
            .padding(top = 13.dp, bottom = 14.dp)
            .padding(horizontal = 16.dp),
        color = colorScheme.tertiary,
        style = typography.labelLarge
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
                userId = "",
                meeting = it,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            ) { onMeetClick(it) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar(
    username: String,
    backButton: Boolean,
    menuState: Boolean,
    isMyProfile: Boolean,
    profileGroup: UserGroupTypeModel,
    onBack: () -> Unit,
    onKebabClick: (Boolean) -> Unit,
    onMenuItemSelect: (Int) -> Unit,
) {
    var badgeWidth by remember { mutableStateOf(IntSize.Zero) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp),
        SpaceBetween, CenterVertically
    ) {
        ConstraintLayout(
            Modifier.fillMaxWidth(),
        ) {
            val (backButtonRef, usernameGroupedRef, menuRef) = createRefs()

            if (backButton) IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .constrainAs(backButtonRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .padding(end = 16.dp)
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_back
                    ),
                    contentDescription = stringResource(
                        R.string.action_bar_button_back
                    ),
                    tint = colorScheme.tertiary
                )
            }

            BoxWithConstraints(modifier = Modifier
                .constrainAs(usernameGroupedRef) {
                    start.linkTo(if (backButton) backButtonRef.end else parent.start)
                    top.linkTo(if (backButton) backButtonRef.top else if (!isMyProfile) menuRef.top else parent.top)
                    if (backButton) bottom.linkTo(backButtonRef.bottom)
                    if (!backButton && !isMyProfile) bottom.linkTo(menuRef.bottom)
                    end.linkTo(if (!isMyProfile) menuRef.start else parent.end)
                    width = Dimension.fillToConstraints
                }) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = username,
                        color = colorScheme.tertiary,
                        style = typography.labelLarge,
                        overflow = Ellipsis,
                        maxLines = 1,
                        modifier =
                        if (profileGroup != DEFAULT) {
                            Modifier
                                .widthIn(min = 1.dp, max = badgeWidth.width.dp)
                                .marquee()
                        } else {
                            Modifier
                                .width(IntrinsicSize.Max)
                                .marquee()

                        },
                    )
                    ProfileBadge(
                        group = profileGroup,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .onSizeChanged { coordinates ->
                                badgeWidth = coordinates
                            }, labelSize = 9,
                        textPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }


            if (!isMyProfile) {
                GDropMenu(
                    menuState = menuState,
                    collapse = { onKebabClick(false) },
                    menuItem = listOf(
                        stringResource(R.string.complaints_title) to
                                { onMenuItemSelect(0) })
                )
                GKebabButton(
                    modifier = Modifier
                        .constrainAs(menuRef) {
                            top.linkTo(if (backButton) backButtonRef.top else parent.top)
                            end.linkTo(parent.end)
                        },
                ) { onKebabClick(true) }
            }
        }
    }
}