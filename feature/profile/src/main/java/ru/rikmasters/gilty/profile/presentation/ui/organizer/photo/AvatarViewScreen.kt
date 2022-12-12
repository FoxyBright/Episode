package ru.rikmasters.gilty.profile.presentation.ui.organizer.photo

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.PhotoView
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

@Composable
fun AvatarScreen(nav: NavState = get()) {
    val profile = DemoProfileModel
    val context = LocalContext.current
    var menuState by remember { mutableStateOf(false) }
    PhotoView(profile.avatar.id, menuState, Modifier, {
        menuState = it
    }, {
        menuState = false
        Toast.makeText(
            context, "Тут будет возможность выбрать другое фото",
            Toast.LENGTH_SHORT
        ).show()
    }) { nav.navigate("main") }
}