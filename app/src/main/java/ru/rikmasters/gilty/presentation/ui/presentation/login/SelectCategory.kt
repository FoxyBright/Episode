package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Composable
@ExperimentalMaterial3Api
fun SelectCategories(modifier: Modifier) {
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
                    .clickable { /*TODO*/ })
            Text(
                stringResource(R.string.interested_you),
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                ThemeExtra.colors.mainTextColor,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                stringResource(R.string.interested_you_details),
                Modifier.padding(),
                ThemeExtra.colors.secondaryTextColor,
                14.sp
            )
        }
        GradientButton(
            { /*TODO*/ },
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            text = stringResource(R.string.next_button),
            enabled = true
        )
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
fun SelectCategoriesPreview() {
    SelectCategories(Modifier)
}