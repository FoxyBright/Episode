package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.MEETING
import ru.rikmasters.gilty.addmeet.viewmodel.CompleteViewModel
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun CompleteScreen(vm: CompleteViewModel) {
    
    val nav= get<NavState>()
    val context = LocalContext.current
    CompleteContent(MEETING, Modifier, object: CompleteCallBack {
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