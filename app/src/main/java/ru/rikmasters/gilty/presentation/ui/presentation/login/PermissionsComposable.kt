package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.shared.CheckBox
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

interface PermissionsContentCallback {
    fun onBack()
    fun onFinish()
}

@Composable
@ExperimentalMaterial3Api
fun PermissionsContent(modifier: Modifier, callback: PermissionsContentCallback) {
    Box(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Image(
                painterResource(R.drawable.ic_back),
                "button back",
                Modifier
                    .padding(top = 32.dp)
                    .clickable { callback.onBack() })
            Text(
                stringResource(R.string.find_more),
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                ThemeExtra.colors.mainTextColor,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                stringResource(R.string.find_more_details),
                Modifier.padding(),
                ThemeExtra.colors.secondaryTextColor,
                14.sp
            )
            Image(
                painterResource(R.drawable.map),
                "map",
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp, horizontal = 38.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                stringResource(R.string.permissions),
                Modifier.padding(bottom = 12.dp),
                ThemeExtra.colors.mainTextColor,
                20.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                Modifier
                    .background(ThemeExtra.colors.elementsBack)
                    .clip(RoundedCornerShape(14.dp))
                    .fillMaxWidth()
            ) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp), Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.geoposition),
                            color = ThemeExtra.colors.mainTextColor,
                            fontSize = 16.sp
                        )
                        CheckBox(Modifier, true)
                    }
                    ViewLine(Modifier.padding(start = 20.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp), Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.notifications),
                            color = ThemeExtra.colors.mainTextColor,
                            fontSize = 16.sp
                        )
                        CheckBox(Modifier, false)
                    }
                }
            }
        }
        GradientButton(
            callback::onFinish,
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            text = stringResource(R.string.finish),
            enabled = true
        )
    }
}

@Composable
private fun ViewLine(modifier: Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ThemeExtra.colors.divider)
    )
}

@Composable
@ExperimentalMaterial3Api
fun PermissionConfirmationWindow() {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ThemeExtra.colors.elementsBack)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(R.drawable.geolocation_icon),
                "geolocation icon",
                Modifier
                    .size(60.dp)
                    .padding(18.dp)
            )
            Text(
                stringResource(R.string.answer_permission),
                Modifier.padding(bottom = 20.dp),
                ThemeExtra.colors.mainTextColor,
                20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            ViewLine(Modifier)
            Text(
                stringResource(R.string.when_using),
                Modifier.padding(vertical = 20.dp),
                ThemeExtra.colors.primary,
                14.sp,
                fontWeight = FontWeight.Bold,
            )
            ViewLine(Modifier)
            Text(
                stringResource(R.string.once),
                Modifier.padding(vertical = 20.dp),
                ThemeExtra.colors.primary,
                14.sp,
                fontWeight = FontWeight.Bold,
            )
            ViewLine(Modifier)
            Text(
                stringResource(R.string.prohibit),
                Modifier.padding(vertical = 20.dp),
                ThemeExtra.colors.primary,
                14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
fun PermissionConfirmationWindowPreview() {
    PermissionConfirmationWindow()
}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
fun PermissionsContentPreview() {
    PermissionsContent(Modifier, object : PermissionsContentCallback {
        override fun onBack() {}
        override fun onFinish() {}
    })
}

