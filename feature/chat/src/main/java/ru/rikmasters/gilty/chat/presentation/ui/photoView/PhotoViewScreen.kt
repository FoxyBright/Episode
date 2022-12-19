package ru.rikmasters.gilty.chat.presentation.ui.photoView

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.PhotoView

@Composable
fun PhotoViewScreen(
    image: String,
    nav: NavState = get()
) {
    PhotoView(
        image, (false), Modifier
    ) { nav.navigate("chat") }
}