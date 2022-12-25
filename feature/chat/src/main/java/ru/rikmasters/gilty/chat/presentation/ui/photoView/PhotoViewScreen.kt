package ru.rikmasters.gilty.chat.presentation.ui.photoView

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        if(type == 1) timer = true    // TODO Реализовать без LaunchEffect
        //        val animateTimer = Animatable(0f)
        //            .animateTo(1f)
        //        nav.navigate("chat")
    }
    val animateTimer = animateFloatAsState(
        if(timer) 1f else 0f, tween(6000)
    ) { nav.navigationBack() }.value
    PhotoView(
        PhotoViewState(
            image, ("1/1"), (false), type, animateTimer
        ), Modifier, object: PhotoViewCallback {
            override fun onBack() {
                nav.navigationBack()
            }
        }
    )
}