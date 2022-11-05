package ru.rikmasters.gilty.profile.presentation.ui

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Profile
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import ru.rikmasters.gilty.shared.shared.MeetingCard
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme


data class UserProfileState(
    val profileState: ProfileState,
    val currentMeetings: List<FullMeetingModel>,
    val meetingsHistory: List<FullMeetingModel>,
    val lastRespond: FullMeetingModel,
    val notifications: Int,
    val historyState: Boolean = false,
    val menuExpanded: Boolean
)

interface UserProfileCallback : ProfileCallback {
    fun menu(state: Boolean) {}
    fun openHistory(state: Boolean) {}
    fun onMeetingClick(meet: MeetingModel) {}
    fun onSettingsClick() {}
    fun onWatchPhotoClick() {}
    fun onRespondsClick() {}
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun UserProfilePreview() {
    GiltyTheme {
        val historyState =
            remember { mutableStateOf(false) }
        val menuExpanded =
            remember { mutableStateOf(false) }
        val meets = DemoMeetingList
        val profileModel = DemoProfileModel
        val state = ProfileState(
            name = "${profileModel.username}, ${profileModel.age}",
            profilePhoto = profileModel.avatar.id,
            description = profileModel.aboutMe,
            rating = profileModel.rating.average,
            observers = 13500,
            observed = 128,
            emoji = EmojiList.first(),
            profileType = ProfileType.USERPROFILE,
            enabled = false,
        )
        UserProfile(
            UserProfileState(
                state, meets, meets, meets.first(), (3),
                historyState.value, menuExpanded.value
            ), Modifier, object : UserProfileCallback {
                override fun menu(state: Boolean) {
                    menuExpanded.value = !menuExpanded.value
                }

                override fun openHistory(state: Boolean) {
                    historyState.value = !state
                }
            }
        )
    }
}

@Composable
fun UserProfile(
    state: UserProfileState,
    modifier: Modifier = Modifier,
    callback: UserProfileCallback? = null
) {
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                IconButton({ callback?.menu(true) }) {
                    Icon(
                        painterResource(R.drawable.ic_kebab),
                        null, Modifier,
                        MaterialTheme.colorScheme.tertiary
                    )
                }
                menu(
                    state.menuExpanded, { callback?.menu(false) },
                    { callback?.onWatchPhotoClick() },
                ) { callback?.onSettingsClick() }
            }
        }
        item { Profile(state.profileState) }
        item {
            Text(
                stringResource(R.string.profile_actual_meetings_label),
                Modifier.padding(top = 28.dp),
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelLarge
            )
        }
        item {
            Responds(
                state.notifications, state.lastRespond,
            ) { callback?.onRespondsClick() }
        }
        if (state.currentMeetings.isNotEmpty()) item {
            LazyRow {
                item { Spacer(Modifier.width(8.dp)) }
                items(state.currentMeetings) {
                    MeetingCard(it, Modifier.padding(horizontal = 4.dp))
                    { callback?.onMeetingClick(it) }
                }
            }
        }
        if (state.meetingsHistory.isNotEmpty()) item {
            meetHistory(state.historyState, state.meetingsHistory,
                { callback?.openHistory(state.historyState) })
            { callback?.onMeetingClick(it) }
        }
    }
}

// TODO вынести в shared
@Composable
private fun menu(
    menuState: Boolean,
    collapse: () -> Unit,
    onWatchPhotoClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val metrics = Resources.getSystem().displayMetrics
    DropdownMenu(
        menuState, collapse,
        Modifier.background(MaterialTheme.colorScheme.primaryContainer),
        DpOffset(((metrics.widthPixels / metrics.density) - 200).dp, 0.dp)
    ) {
        DropdownMenuItem(
            { menuItem(stringResource(R.string.profile_menu_watch_photo_button)) },
            onWatchPhotoClick
        )
        DropdownMenuItem(
            { menuItem(stringResource(R.string.profile_menu_settings_button)) },
            onSettingsClick
        )
    }
}

@Composable
private fun menuItem(text: String, modifier: Modifier = Modifier) {
    Text(
        text, modifier, MaterialTheme.colorScheme.tertiary,
        style = MaterialTheme.typography.bodyMedium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Responds(
    size: Int,
    last: FullMeetingModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    last.organizer.id, (null), Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    stringResource(R.string.profile_responds_label),
                    Modifier.padding(start = 16.dp),
                    MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        size.toString(),
                        Modifier.padding(12.dp, 6.dp), Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.profile_responds_label),
                    Modifier, MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
private fun meetHistory(
    historyState: Boolean,
    historyList: List<FullMeetingModel>,
    openHistory: () -> Unit,
    onSelect: (FullMeetingModel) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 28.dp)
            .clip(CircleShape)
            .clickable { openHistory() },
        Arrangement.Start, Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.profile_meeting_history_label),
            Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.titleLarge
        )
        Icon(
            if (!historyState) Icons.Filled.KeyboardArrowRight
            else Icons.Filled.KeyboardArrowDown,
            stringResource(R.string.profile_responds_label),
            tint = MaterialTheme.colorScheme.onTertiary
        )
    }
    if (historyState) LazyRow {
        item { Spacer(Modifier.width(8.dp)) }
        items(historyList) {
            MeetingCard(it, Modifier.padding(horizontal = 4.dp))
            { onSelect(it) }
        }
    }
}