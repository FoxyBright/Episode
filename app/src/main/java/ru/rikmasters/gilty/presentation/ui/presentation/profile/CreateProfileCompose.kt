package ru.rikmasters.gilty.presentation.ui.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun ProfilePreview() {
    GiltyTheme() {
        CreateProfile(value = "", callback =  object : CreateProfileCallback{
            override fun profileImage() {

            }

            override fun hiddenImages() {
            }

            override fun back() {
            }

        })
    }

}

interface CreateProfileCallback {

    fun profileImage()

    fun hiddenImages()

    fun back()
}

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {



        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfile(
    modifier: Modifier = Modifier,
    value: String,
    callback: CreateProfileCallback
) {

        Box(Modifier.fillMaxSize()) {

            Column() {
                Image(
                    painterResource(id =  R.drawable.ic_back),
                    contentDescription = "button back",
                    Modifier
                        .padding(start = 16.dp, top = 32.dp)
                        .clickable { callback.back() })

                Text(
                    text = stringResource(id = R.string.create_profile_title),
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge, color = ThemeExtra.colors.mainTextColor )

                Card(
                    modifier =
                    Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 32.dp)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    border = BorderStroke(6.dp, Color.White)) {

                    Column(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize()) {
                        TextField(
                            value = "",
                            onValueChange = {},
                            colors = colors(),
                            modifier = Modifier
                                .padding(top = 54.dp)
                                .fillMaxWidth(),
                            placeholder = {

                                Row(horizontalArrangement = Arrangement.Center) {

                                    Text(
                                        stringResource(R.string.user_name),
                                        Modifier.padding(end = 8.dp),
                                        color = ThemeExtra.colors.secondaryTextColor,
                                        fontSize = 23.sp,
                                        lineHeight = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Icon(
                                        painterResource(id = R.drawable.ic_edit ),
                                        "edit",
                                        Modifier.padding(top = 4.dp),
                                        tint = ThemeExtra.colors.grayIcon)

                                }

                            },
                            singleLine = true
                        )

                        Row() {

                            ProfileImageContent(createProfileCallback = callback)

                            Spacer(modifier = Modifier.width(14.dp))
                            Column {

                                ProfileStatContent()

                                Spacer(modifier = Modifier.height(14.dp))

                                HiddenPhotoContent()
                            }
                        }

                        Text(
                            text = stringResource(R.string.about_me),
                            Modifier.padding(top = 20.dp),
                            fontSize = 17.sp,
                            color = Color.Black, fontWeight = FontWeight.Bold)

                        TextField(
                            "",
                            {  },
                            Modifier.padding(top = 12.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = ThemeExtra.colors.cardBackground,
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
                                focusedLabelColor = ThemeExtra.colors.mainTextColor,
                                focusedIndicatorColor = Color.Transparent),
                            placeholder = {
                                Text(
                                    stringResource(R.string.about_me_placeholder),
                                    color = ThemeExtra.colors.secondaryTextColor, fontWeight = FontWeight.Medium
                                )
                            },

                            singleLine = false,
                        )
                    }

                }

            }

            GradientButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 48.dp)
                    .align(Alignment.BottomCenter),
                onClick = { /*TODO*/ },
                text = stringResource(R.string.next_button),
                enabled = true)
        }

    }



@Composable
private fun colors() = TextFieldDefaults.textFieldColors(
    containerColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
    focusedLabelColor = ThemeExtra.colors.mainTextColor,
    focusedIndicatorColor = Color.Transparent,
)