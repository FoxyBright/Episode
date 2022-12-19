package ru.rikmasters.gilty.profile.presentation.ui.organizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileCallback
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Profile
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import ru.rikmasters.gilty.shared.shared.MeetingCategoryCard
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class OrganizerProfileState(
    val profileState: ProfileState,
    val currentMeetings: List<MeetingModel>,
)

@Preview
@Composable
private fun OrganizerProfilePreview() {
    GiltyTheme {
        var observeState by remember { mutableStateOf(false) }
        val currentMeetings = DemoMeetingList
        val profileModel = DemoProfileModel
        val profileState = ProfileState(
            name = "${profileModel.username}, ${profileModel.age}",
            profilePhoto = profileModel.avatar.id,
            description = profileModel.aboutMe,
            rating = profileModel.rating.average,
            emoji = EmojiList.first(),
            profileType = ProfileType.ORGANIZER,
            observeState = observeState,
            enabled = false
        )
        OrganizerProfile(
            Modifier, OrganizerProfileState(profileState, currentMeetings),
            object : UserProfileCallback {
                override fun closeAlert() {}

                override fun onObserveChange(state: Boolean) {
                    super.onObserveChange(observeState)
                    observeState = state
                }
            }
        )
    }
}

@Composable
fun OrganizerProfile(
    modifier: Modifier = Modifier,
    state: OrganizerProfileState,
    callback: UserProfileCallback? = null
) {
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Profile(
                state.profileState,
                Modifier.padding(horizontal = 16.dp),
                callback
            ) { callback?.onObserveChange(it) }
        }
        if (state.currentMeetings.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.profile_actual_meetings_label),
                    Modifier
                        .padding(top = 28.dp, bottom = 14.dp)
                        .padding(horizontal = 16.dp),
                    MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                LazyRow {
                    item { Spacer(Modifier.width(8.dp)) }
                    items(state.currentMeetings) {
                        MeetingCategoryCard(it, Modifier.padding(horizontal = 4.dp))
                        { callback?.onMeetingClick(it) }
                    }
                }
            }
        }
    }
}