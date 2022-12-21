package ru.rikmasters.gilty.chat.presentation.ui.photoView

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.PhotoView
import ru.rikmasters.gilty.shared.common.PhotoViewCallback
import ru.rikmasters.gilty.shared.common.PhotoViewState

@Composable
fun PhotoViewScreen(
    image: String,
    type: Int,
    nav: NavState = get()
) {
    var timer by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(Unit) {
        while(timer <= 1f) {
            delay(10L)
            timer += 0.005f
        }
    }
    
    PhotoView(
        PhotoViewState(
            image, (false),
            type, timer
        ), Modifier, object: PhotoViewCallback {
            override fun onBack() {
                nav.navigate("chat")
            }
        }
    )
}