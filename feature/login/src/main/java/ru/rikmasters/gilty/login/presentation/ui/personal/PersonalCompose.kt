package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.NumberPicker
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
fun PersonalInfoPreview() {
    GiltyTheme { PersonalInfoContent(PersonalInfoContentState(18, listOf())) }
}

interface PersonalInfoContentCallback : NavigationInterface {
    fun onAgeChange(it: Int) {}
    fun onGenderChange(index: Int) {}
    fun onAgeClick() {}
}

data class PersonalInfoContentState(
    val age: Int? = null,
    val list: List<Boolean>
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PersonalInfoContent(
    state: PersonalInfoContentState,
    callback: PersonalInfoContentCallback? = null,
) {
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Column {
            ActionBar(stringResource(R.string.personal_info_title))
            { callback?.onBack() }
            Text(
                stringResource(R.string.how_old_are_you),
                Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp),
                colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge
            )
            Card(
                { callback?.onAgeClick() },
                Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(colorScheme.primaryContainer)
            ) {
                Text(
                    "${state.age ?: stringResource(R.string.personal_info_age_placeholder)}",
                    Modifier.padding(16.dp), if (state.age == null)
                        colorScheme.onTertiary else colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                stringResource(R.string.sex),
                Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp),
                colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge
            )
            Card(
                Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp),
                MaterialTheme.shapes.large,
                CardDefaults.cardColors(colorScheme.primaryContainer)
            ) {
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    itemsIndexed(state.list) { index, it ->
                        GiltyChip(
                            Modifier.padding(end = 12.dp),
                            stringResource(
                                listOf(
                                    R.string.female_sex,
                                    R.string.male_sex,
                                    R.string.others_sex
                                )[index]
                            ), it
                        ) { callback?.onGenderChange(index) }
                    }
                }
            }
        }
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button),
            /*(state.list.contains(true) && state.age != null)*/ true
        ) { callback?.onNext() }
    }
}

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (Int) -> Unit,
    range: IntRange = 18..100,
    onSave: () -> Unit
) {
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45f)
            .padding(16.dp),
        Alignment.TopCenter
    ) {
        NumberPicker(
            Modifier.padding(top = 40.dp),
            value = value ?: range.first,
            onValueChange = { onValueChange(it) },
            range = range
        )
        GradientButton(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            stringResource(R.string.save_button), true
        ) { onSave() }
    }
}