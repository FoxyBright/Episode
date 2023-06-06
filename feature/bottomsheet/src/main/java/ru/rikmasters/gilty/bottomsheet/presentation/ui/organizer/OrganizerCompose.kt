package ru.rikmasters.gilty.bottomsheet.presentation.ui.organizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.gallery.photoview.PhotoView
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTO
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Profile
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.profileBadges.ProfileBadge
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.GDropMenu
import ru.rikmasters.gilty.shared.shared.GKebabButton
import ru.rikmasters.gilty.shared.shared.MeetingCategoryCard
import ru.rikmasters.gilty.shared.shared.itemSpacer
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
            "${user?.username}${
                if (user?.age in 18..99) {
                    ", ${user?.age}"
                } else ""
            }",
            state.backButton, state.menuState,
            state.isMyProfile,
            state.profileState.profile?.group?:UserGroupTypeModel.DEFAULT,
            { callback?.onBack() },
            { callback?.onMenuDismiss(it) },
        ) { callback?.onMenuItemSelect(it, user?.id) }
        LazyColumn(
            modifier
                .fillMaxWidth()
        ) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
            item {
                Profile(
                    state = state.profileState,
                    callback = callback
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
        if (state.photoViewState) PhotoView(
            images = state.viewerImages,
            selected = state.viewerSelectImage,
            menuState = state.viewerMenuState,
            type = PHOTO,
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
            .padding(top = 28.dp, bottom = 14.dp)
            .padding(horizontal = 16.dp),
        color = colorScheme.tertiary,
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
                userId = "",
                meeting = it,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
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
    profileGroup:UserGroupTypeModel,
    onBack: () -> Unit,
    onKebabClick: (Boolean) -> Unit,
    onMenuItemSelect: (Int) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            Start, CenterVertically
        ) {
            if (backButton) IconButton(
                onClick = { onBack() },
                modifier = Modifier
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
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = username,
                    color = colorScheme.tertiary,
                    style = typography.labelLarge,
                    overflow = Ellipsis,
                    maxLines = 1
                )
                ProfileBadge(group = profileGroup, modifier = Modifier.padding(start = 4.dp))
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
            GKebabButton { onKebabClick(true) }
        }
    }
}