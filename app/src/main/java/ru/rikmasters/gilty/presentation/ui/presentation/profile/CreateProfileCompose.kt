package ru.rikmasters.gilty.presentation.ui.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.shared.LoginActionBar
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun ProfilePreview() {
    GiltyTheme {
        CreateProfile(Modifier, "", object : CreateProfileCallback {
            override fun profileImage() {}
            override fun hiddenImages() {}
            override fun onBack() {}
            override fun onNext() {}
        })
    }
}

interface CreateProfileCallback : NavigationInterface {
    fun profileImage() {}
    fun hiddenImages() {}
}

@Composable
fun Screen(
    modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit
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
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LoginActionBar(
                stringResource(R.string.create_profile_title)
            )
            { callback.onBack() }
            Card(
                Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                RoundedCornerShape(30.dp),
                CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                border = BorderStroke(6.dp, Color.White)
            ) {
                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                ) {
                    TextField(
                        "",
                        {},
                        Modifier
                            .padding(top = 54.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
                            focusedLabelColor = ThemeExtra.colors.mainTextColor,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Row(horizontalArrangement = Arrangement.Center) {
                                Text(
                                    stringResource(R.string.user_name),
                                    Modifier.padding(end = 8.dp),
                                    ThemeExtra.colors.secondaryTextColor,
                                    23.sp,
                                    lineHeight = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    painterResource(R.drawable.ic_edit),
                                    "edit",
                                    Modifier.padding(top = 4.dp),
                                    ThemeExtra.colors.grayIcon
                                )
                            }
                        },
                        singleLine = true
                    )
                    Row {
                        ProfileImageContent(callback)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            ProfileStatContent()
                            Spacer(Modifier.height(14.dp))
                            HiddenPhotoContent()
                        }
                    }
                    Text(
                        stringResource(R.string.about_me),
                        Modifier.padding(top = 20.dp),
                        Color.Black,
                        17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        "",
                        { },
                        Modifier.padding(top = 12.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = ThemeExtra.colors.cardBackground,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
                            focusedLabelColor = ThemeExtra.colors.mainTextColor,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                stringResource(R.string.about_me_placeholder),
                                color = ThemeExtra.colors.secondaryTextColor,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        singleLine = false,
                    )
                }
            }
        }
        GradientButton(
            callback::onNext,
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button),
            true
        )
    }
}