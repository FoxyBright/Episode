package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.core.GiltyChip
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.ActionBar
import ru.rikmasters.gilty.presentation.ui.shared.BottomSheetCompose
import ru.rikmasters.gilty.presentation.ui.shared.BottomSheetComposeState
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.shared.NumberPicker
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Composable
@ExperimentalMaterial3Api
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
fun PersonalInfoPreview() {
    GiltyTheme {
        PersonalInfoContent()
    }
}

@Composable
@ExperimentalMaterial3Api
fun PersonalInfoContent(callback: NavigationInterface? = null) {
    val bottomSheetState = remember { mutableStateOf(false) }
    val ageTextFieldValue = remember { mutableStateOf("") }
    var pickerValue by remember { mutableStateOf(18) }
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ActionBar(
                stringResource(R.string.personal_info_title)
            )
            { callback?.onBack() }
            Text(
                stringResource(R.string.how_old_are_you),
                Modifier.padding(top = 24.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H3
            )
            TextField(
                ageTextFieldValue.value, {},
                Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .clickable { bottomSheetState.value = !bottomSheetState.value },
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = ThemeExtra.colors.cardBackground,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = ThemeExtra.colors.secondaryTextColor,
                    disabledTextColor = ThemeExtra.colors.mainTextColor
                ),
                singleLine = true,
                enabled = false
            )
            Text(
                stringResource(R.string.sex),
                Modifier.padding(top = 24.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H3
            )
            val chipsTitle =
                remember { listOf(R.string.male_sex, R.string.female_sex, R.string.others_sex) }
            Card(
                Modifier.padding(top = 12.dp),
                MaterialTheme.shapes.large,
                CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                val list = remember { mutableStateListOf(false, false, false) }
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    itemsIndexed(list) { index, it ->
                        GiltyChip(
                            Modifier.padding(end = 12.dp),
                            stringResource(chipsTitle[index]), it
                        ) {
                            for (i in 0..list.lastIndex) {
                                list[i] = false
                            }
                            list[index] = true
                        }
                    }
                }
            }
        }
        GradientButton(
            { callback?.onNext() },
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button)
        )
        BottomSheetCompose(
            BottomSheetComposeState(320.dp, bottomSheetState) {
                NumberPicker(
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp),
                    value = pickerValue,
                    onValueChange = { pickerValue = it },
                    range = 18..100
                )
                GradientButton(
                    {
                        ageTextFieldValue.value = pickerValue.toString()
                        bottomSheetState.value = false
                    },
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                        .padding(top = 40.dp),
                    stringResource(R.string.save_button), true
                )
            }, Modifier
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .align(Alignment.BottomCenter)
        ) {
            bottomSheetState.value = false
        }
    }
}