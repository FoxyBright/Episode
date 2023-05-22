package ru.rikmasters.gilty.profile.presentation.ui.album_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.model.image.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.theme.Colors

data class AlbumDetailsState(
    val image: String,
    val topEmoji: EmojiModel
)

interface AlbumDetailsCallback {
    fun onBack()
    fun onEmojiClick(emojiModel: EmojiModel)
}

@Composable
fun AlbumDetailsContent(
    state: AlbumDetailsState,
    callback: AlbumDetailsCallback? = null,
    modifier: Modifier = Modifier,
) {
    val asm = get<AppStateModel>()

    LaunchedEffect(key1 = Unit, block = {
        asm.systemUi.setStatusBarColor(Colors.PreDark)
    })

    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            asm.systemUi.setStatusBarColor(Colors.White)
        }
    })

    Column(modifier = Modifier.fillMaxSize()) {
        ActionBar {
            callback?.onBack()
        }
        Album(state = state, callback = callback)
    }
}

@Composable
private fun Album(
    state: AlbumDetailsState,
    callback: AlbumDetailsCallback? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Spacer(modifier = Modifier.weight(0.12f))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            GCachedImage(
                modifier = Modifier,
                url = "https://media.glamourmagazine.co.uk/photos/631867c109fbe8cda0c9b9da/1:1/w_1920,h_1920,c_limit/HARRYSTYLES%20CHRISPINE%20SPIT%20070922%20default-sq-GettyImages-1421314259.jpg",
                contentScale = ContentScale.Crop
            )
            AlbumIcon(modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 16.dp).size(52.dp),
                onClick = { callback?.onEmojiClick(DemoEmojiModel) },
                content = {
                GEmojiImage(modifier = Modifier.size(32.dp).padding(4.dp), emoji = state.topEmoji)
            })

            Column(
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                AlbumIcon(modifier = Modifier.size(52.dp), onClick = {
                    callback?.onEmojiClick(DemoEmojiModel)
                }, content = {
                    Image(
                        painterResource(R.drawable.ic_open_eye),
                        (null), Modifier
                            .size(32.dp)
                            .padding(4.dp)
                    )
                })

                Spacer(modifier = Modifier.height(12.dp))

                AlbumIcon(modifier = Modifier.size(52.dp), onClick = {
                    callback?.onEmojiClick(DemoEmojiModel)
                }, content = {
                    Image(
                        painterResource(R.drawable.ic_image_box),
                        (null), Modifier
                            .size(32.dp)
                            .padding(4.dp)
                    )
                })

            }
        }
        Spacer(modifier = Modifier.weight(0.12f))
    }
}

@Composable
fun AlbumIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0x88000000))
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        content()
    }

}

@Composable
private fun ActionBar(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.PreDark)
    ) {
        IconButton(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .padding(start = 12.dp), onClick = onClick
        ) {
            Icon(
                painterResource(
                    R.drawable.ic_back
                ),
                stringResource(R.string.action_bar_button_back),
                tint = Color(0xffE6E1E5)
            )
        }
    }
}