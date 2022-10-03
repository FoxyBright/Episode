package ru.rikmasters.gilty.presentation.ui.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenPhotoContent() {

    Surface(
        onClick = {},
        modifier = Modifier
            .height(93.dp)
            .wrapContentWidth()
            .clip(MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        color = Color.White) {

        Column() {

            Box(
                Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .size(26.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Image(
                    painterResource(R.drawable.ic_lock_open),
                    null,
                    Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }


            Box(Modifier.padding(top = 25.dp)) {
                ImageCardBottom(text = stringResource(id = R.string.hidden_image))
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageContent(
    createProfileCallback: CreateProfileCallback ) {

    Surface(
        onClick = {},
        modifier = Modifier
            .height(200.dp)
            .wrapContentWidth()
            .clip(MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        color = Color.White) {

        Box(contentAlignment = Alignment.BottomCenter) {
            ImageCardBottom(text = stringResource(id = R.string.user_image))
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun ProfileStatContent() {

    Card(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .height(93.dp)
            .wrapContentWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {

        val gradientColor = Brush.horizontalGradient(
            0f to ThemeExtra.colors.gradientColor1,
            1000f to ThemeExtra.colors.gradientColor2)

        Column() {

            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .wrapContentHeight()) {
                
                Text(
                    text = "4.9",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(brush = gradientColor))

                Image(
                    painter = painterResource(id = R.drawable.ic_emoji),
                    contentDescription = null, Modifier.padding(top = 14.dp))
            }

            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)) {
                
                Column(Modifier.weight(1f)) {
                    Text(text = "0", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 12.sp)
                    
                    Text(text = stringResource(R.string.observers), fontSize = 8.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                
                Column(Modifier.weight(1f)) {

                    Text(text = "0", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 12.sp)

                    Text(text = stringResource(R.string.observe), fontSize = 8.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageCardBottom(
    modifier: Modifier = Modifier,
    text: String) {

    Row(
        modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = text,
            Modifier.padding(end = 4.dp),
            fontSize = 12.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = ThemeExtra.colors.secondaryTextColor)

        Box(
            Modifier
                .size(26.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Image(
                painterResource(R.drawable.ic_image_box),
                null,
                Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }
    }


}