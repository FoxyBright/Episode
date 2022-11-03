package ru.rikmasters.gilty.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Profile
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.shared.MeetingCard
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class OrganizerProfileState(
    val profileModel: ProfileModel,
    val profileState: ProfileState,
    val currentMeetings: List<FullMeetingModel>,
)

@Preview
@Composable
private fun OrganizerProfilePreview() {
    GiltyTheme {
        var observeState by remember { mutableStateOf(false) }
        OrganizerProfile(
            Modifier,
            OrganizerProfileState(
                DemoProfileModel,
                ProfileState(observeState = observeState),
                listOf(DemoFullMeetingModel, DemoFullMeetingModel, DemoFullMeetingModel)
            ), object : UserProfileCallback {
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
            .padding(16.dp)
    ) {
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
                    ProfileType.ORGANIZER,
                    state.profileState.observeState,
                    false,
                )
            ) { callback?.onObserveChange(it) }
        }
        if (state.currentMeetings.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.profile_actual_meetings_label),
                    Modifier.padding(top = 28.dp, bottom = 14.dp),
                    MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.titleLarge
                )
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
        }
    }
}