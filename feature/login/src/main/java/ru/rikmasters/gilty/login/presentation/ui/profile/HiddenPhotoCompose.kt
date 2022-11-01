package ru.rikmasters.gilty.login.presentation.ui.profile

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import java.io.File

@Preview(showBackground = true)
@Composable
private fun HiddenPhotoPreview() {
    GiltyTheme {
        HiddenPhotoContent(HiddenPhotoState(listOf(File(""))))
    }
}

data class HiddenPhotoState(
    val photoList: List<File>
)

interface HiddenPhotoCallback : NavigationInterface {
    fun onSelectImage(file: File) {}
    fun onDeleteImage(file: File) {}
    fun openGallery() {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HiddenPhotoContent(
    state: HiddenPhotoState,
    modifier: Modifier = Modifier,
    callback: HiddenPhotoCallback? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(horizontal = 16.dp)
    ) {
        ActionBar(
            "Скрытые фото",
            "Доступ к фото будет доступен при Вашем\nразрешении",
            Modifier.padding(bottom = 20.dp)
        ) { callback?.onBack() }
        LazyVerticalGrid(
            GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(state.photoList) { file ->
                LazyItem(
                    file, Modifier,
                    { callback?.onSelectImage(it) })
                { callback?.onDeleteImage(it) }
            }
            item {
                Card(
                    { callback?.openGallery() },
                    Modifier.size(130.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(ThemeExtra.colors.grayButton),
                        Center
                    ) {
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_image_box),
                                null,
                                Modifier
                                    .padding(6.dp)
                                    .size(32.dp), Color.White
                            )
                        }
                    }
                }
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button)
        ) { callback?.onNext() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LazyItem(
    file: File,
    modifier: Modifier = Modifier,
    onSelect: (File) -> Unit,
    onDelete: (File) -> Unit
) {
    val image =
        BitmapFactory.decodeFile(file.canonicalPath)
    Box(
        modifier
            .size(130.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable { onSelect(file) },
        TopEnd
    ) {
        Image(
            image.asImageBitmap(), (null),
            Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Card(
            { onDelete(file) },
            Modifier
                .padding(4.dp)
                .size(26.dp)
                .clip(CircleShape)
                .align(TopEnd)
                .alpha(50f), colors = CardDefaults.cardColors(Color.Transparent)
        ) {
            Box(Modifier, Center) {
                Image(
                    painterResource(R.drawable.transparency_circle),
                    (null), Modifier.fillMaxSize()
                )
                Icon(Icons.Filled.Close, null, Modifier.size(16.dp), Color.White)
            }
        }
    }
}