package ru.rikmasters.gilty.presentation.ui.presentation.profile

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.enumeration.ProfileType
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.MeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.presentation.model.profile.DemoProfileModel
import ru.rikmasters.gilty.presentation.model.profile.ProfileModel
import ru.rikmasters.gilty.presentation.ui.shared.MeetingCard
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


data class UserProfileState(
    val profileModel: ProfileModel,
    val profileState: ProfileState,
    val currentMeetings: List<ShortMeetingModel>,
    val meetingsHistory: List<ShortMeetingModel>,
    val lastRespond: ShortMeetingModel,
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

@Preview(showBackground = true)
@Composable
private fun UserProfilePreview() {
    GiltyTheme {
        val historyState = remember { mutableStateOf(false) }
        val menuExpanded = remember { mutableStateOf(false) }
        UserProfile(
            UserProfileState(
                DemoProfileModel,
                ProfileState(observers = 13500, observed = 128),
                listOf(DemoMeetingModel, DemoMeetingModel, DemoMeetingModel),
                listOf(DemoMeetingModel, DemoMeetingModel),
                DemoMeetingModel,
                3,
                historyState.value,
                menuExpanded.value
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

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(16.dp)
    ) {
        item {
            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                IconButton({ callback?.menu(true) }) {
                    Icon(
                        painterResource(R.drawable.ic_kebab),
                        null, Modifier,
                        ThemeExtra.colors.mainTextColor
                    )
                }
                val metrics = Resources.getSystem().displayMetrics
                DropdownMenu(
                    state.menuExpanded,
                    { callback?.menu(false) },
                    Modifier.background(ThemeExtra.colors.cardBackground),
                    DpOffset(((metrics.widthPixels / metrics.density) - 200).dp, 0.dp)
                ) {
                    DropdownMenuItem(
                        {
                            Text(
                                stringResource(R.string.profile_menu_watch_photo_button),
                                color = ThemeExtra.colors.mainTextColor,
                                style = ThemeExtra.typography.Body1Medium
                            )
                        },
                        { callback?.onWatchPhotoClick() }
                    )
                    DropdownMenuItem(
                        {
                            Text(
                                stringResource(R.string.profile_menu_settings_button),
                                color = ThemeExtra.colors.mainTextColor,
                                style = ThemeExtra.typography.Body1Medium
                            )
                        },
                        { callback?.onSettingsClick() }
                    )
                }
            }
        }
        item {
            Profile(
                ProfileState(
                    "${state.profileModel.username}, ${state.profileModel.age}",
                    state.profileState.hiddenPhoto,
                    state.profileModel.avatar.id,
                    state.profileState.lockState,
                    state.profileModel.aboutMe,
                    state.profileModel.rating.average,
                    state.profileState.observers,
                    state.profileState.observed,
                    state.profileModel.emoji,
                    ProfileType.USERPROFILE,
                    false,
                )
            )
        }
        item {
            Text(
                stringResource(R.string.profile_actual_meetings_label),
                Modifier.padding(top = 28.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H3
            )
        }
        item {
            Card(
                { callback?.onRespondsClick() },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
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
                            state.lastRespond.organizer.id, null,
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            stringResource(R.string.profile_responds_label),
                            Modifier.padding(start = 16.dp),
                            color = ThemeExtra.colors.mainTextColor,
                            style = ThemeExtra.typography.SubHeadMedium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                "${state.notifications}",
                                Modifier.padding(12.dp, 6.dp),
                                Color.White,
                                style = ThemeExtra.typography.SubHeadSb
                            )
                        }
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            stringResource(R.string.profile_responds_label),
                            Modifier,
                            ThemeExtra.colors.secondaryTextColor
                        )
                    }
                }
            }
        }
        item {
            LazyRow {
                itemsIndexed(state.currentMeetings) { index, it ->
                    MeetingCard(
                        it,
                        Modifier.padding(
                            end = if (index <= state.currentMeetings.size - 2) 8.dp else 0.dp
                        )
                    ) { callback?.onMeetingClick(it) }
                }
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .clip(CircleShape)
                    .clickable { callback?.openHistory(state.historyState) },
                Arrangement.Start, Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.profile_meeting_history_label),
                    Modifier.padding(8.dp),
                    color = ThemeExtra.colors.mainTextColor,
                    style = ThemeExtra.typography.H3
                )
                Icon(
                    if (!state.historyState) Icons.Filled.KeyboardArrowRight
                    else Icons.Filled.KeyboardArrowDown,
                    stringResource(R.string.profile_responds_label),
                    tint = ThemeExtra.colors.secondaryTextColor
                )
            }
            if (state.historyState)
                LazyRow {
                    itemsIndexed(state.meetingsHistory) { index, it ->
                        MeetingCard(
                            it,
                            Modifier.padding(
                                end = if (index <= state.meetingsHistory.size - 2) 8.dp else 0.dp
                            )
                        ) { callback?.onMeetingClick(it) }
                    }
                }
        }
    }
}