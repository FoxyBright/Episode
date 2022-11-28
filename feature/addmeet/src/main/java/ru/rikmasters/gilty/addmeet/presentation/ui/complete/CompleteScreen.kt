package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel

@Composable
fun CompleteScreen(nav: NavState = get()) {
    val meet = DemoFullMeetingModel
    val context = LocalContext.current
    CompleteContent(meet, Modifier, object : CompleteCallBack {
        override fun onShare() {
            Toast.makeText(
                context,
                "ВААААУ, Типа поделился) Съешь пирожок с полки",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onClose() {
            nav.navigateAbsolute("main/meetings")
        }
    })
}