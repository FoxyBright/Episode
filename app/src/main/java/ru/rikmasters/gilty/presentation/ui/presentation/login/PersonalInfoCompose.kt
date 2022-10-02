package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.core.GiltyChip
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun PersonalInfoPreview() {

    GiltyTheme {
        PersonalInfoContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoContent() {
    
    Box(
        Modifier
            .fillMaxSize()) {
        
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)) {

            Image(
                painterResource(id =  R.drawable.ic_back),
                contentDescription = "button back",
                Modifier
                    .padding(top = 32.dp)
                    .clickable { })

            Text(
                text = stringResource(id = R.string.personal_info_title),
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                color = ThemeExtra.colors.mainTextColor )
            
            Text(
                text = stringResource(id = R.string.how_old_are_you),
                Modifier.padding(top = 24.dp),
                fontSize = 20.sp,
                color = ThemeExtra.colors.mainTextColor, fontWeight = FontWeight.Bold
            )

            TextField(
                "",
                {  },
                Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = ThemeExtra.colors.cardBackground,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
                    focusedLabelColor = ThemeExtra.colors.mainTextColor,
                    focusedIndicatorColor = Color.Transparent),

                singleLine = true,
            )

            Text(
                text = stringResource(id = R.string.sex),
                Modifier.padding(top = 24.dp),
                fontSize = 20.sp,
                color = ThemeExtra.colors.mainTextColor, fontWeight = FontWeight.Bold
            )

            val chipsTitle = remember { listOf(R.string.male_sex, R.string.female_sex, R.string.others_sex) }

            Card(
                modifier = Modifier.padding(top = 12.dp),
                shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(
                    containerColor = ThemeExtra.colors.cardBackground
                )) {

                val list = remember { mutableStateListOf(false, false, false) }

                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                    itemsIndexed(list) { i, it ->
                        GiltyChip(
                            Modifier.padding(end = 12.dp),
                            stringResource(id = chipsTitle[i]), it
                        ) { for (i1 in 0..list.lastIndex) { list[i1] = false }
                            list[i] = true }
                    }
                }
            }

        }

        GradientButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            text = stringResource(id = R.string.next_button),
            enabled = true)
    }
}