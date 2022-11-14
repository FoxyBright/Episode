package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(showBackground = true)
@Composable
private fun CrossButtonPreview() {
    GiltyTheme {
        CrossButton(
            Modifier
                .padding(4.dp)
                .size(20.dp)
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun TransparentCrossButtonPreview() {
    GiltyTheme {
        Box(Modifier.size(50.dp), Alignment.TopEnd) {
            Image(
                painterResource(R.drawable.gb),
                (null), Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            CrossButton(
                Modifier
                    .padding(4.dp)
                    .size(20.dp), (true)
            ) {}
        }
    }
}

@Composable
fun CrossButton(
    modifier: Modifier = Modifier,
    transparent: Boolean = false,
    size: Dp = 16.dp,
    onClick: () -> Unit
) {
    Box(
        modifier
            .clip(CircleShape)
            .clickable { onClick() },
        Alignment.Center
    ) {
        if (transparent)
            Image(
                painterResource(R.drawable.transparency_circle),
                (null), Modifier.fillMaxSize()
            )
        else Box(
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.outline,
                    CircleShape
                )
        )
        Icon(
            Icons.Filled.Close, (null),
            Modifier.size(size), MaterialTheme.colorScheme.background
        )
    }
}