package ru.rikmasters.gilty.profile.presentation.ui.organizer

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.profile.presentation.ui.user.MyMeetingScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
@Suppress("unused")
fun OrganizerProfileScreen() {
    GiltyTheme {
        val asm = get<AppStateModel>()
        val scope = rememberCoroutineScope()
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
        
        val context = LocalContext.current
        
        val organizerCallback = object: UserProfileCallback {
            
            override fun menu(state: Boolean) {}
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        MyMeetingScreen(profileModel, meet, asm, scope)
                    }
                }
            }
            
            override fun onBack() {
                Toast.makeText(
                    context,
                    "Зачем тыкать - если можно дернуть?",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            override fun closeAlert() {}
            
            override fun onObserveChange(state: Boolean) {
                super.onObserveChange(observeState)
                observeState = state
            }
        }
        OrganizerProfile(
            Modifier, OrganizerProfileState(
                profileState, currentMeetings
            ), organizerCallback
        )
    }
}