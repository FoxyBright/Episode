package ru.rikmasters.gilty.profile.presentation.ui.album_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.AlbumDetailsViewModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel

@Composable
fun AlbumDetailsScreen(vm: AlbumDetailsViewModel, albumId: Int) {
    val nav = get<NavState>()

    val image by vm.image.collectAsState()
    val emoji by vm.topEmoji.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        vm.loadAlbum(albumId)
    })

    AlbumDetailsContent(
        AlbumDetailsState(image, emoji),
        object : AlbumDetailsCallback {
            override fun onBack() {
                nav.navigationBack()
            }

            override fun onEmojiClick(emojiModel: EmojiModel) {

            }
        }
    )
}