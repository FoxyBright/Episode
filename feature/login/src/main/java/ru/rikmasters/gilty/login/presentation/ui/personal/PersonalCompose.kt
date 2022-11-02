package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
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
    GiltyTheme {
        PersonalInfoContent(
            PersonalInfoContentState(
                rememberCoroutineScope(),
                18, "", listOf()
            )
        )
    }
}

interface PersonalInfoContentCallback : NavigationInterface {
    fun onAgeChange(it: Int) {}
    fun onGenderChange(index: Int) {}
}

data class PersonalInfoContentState(
    val scope: CoroutineScope,
    val age: Int,
    val ageText: String,
    val list: List<Boolean>
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PersonalInfoContent(
    state: PersonalInfoContentState,
    callback: PersonalInfoContentCallback? = null,
    asm: AppStateModel = get()
) {
    // TODO Вынести состояние за пределы контента
    var age by remember { mutableStateOf(state.age) }
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            ActionBar(stringResource(R.string.personal_info_title))
            { callback?.onBack() }
            Text(
                stringResource(R.string.how_old_are_you),
                Modifier.padding(top = 24.dp),
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge
            )
            TextField(
                state.ageText, {},
                Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .clickable {
                        state.scope.launch {
                            asm.bottomSheetState.expand {
                                BottomSheetContent(Modifier, age, { age = it })
                                {
                                    state.scope.launch {
                                        callback?.onAgeChange(age)
                                        asm.bottomSheetState.collapse()
                                    }
                                }
                            }
                        }
                    },
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = MaterialTheme.colorScheme.onTertiary,
                    disabledTextColor = MaterialTheme.colorScheme.tertiary
                ),
                placeholder = {
                    Text(
                        stringResource(R.string.personal_info_age_placeholder),
                        color = MaterialTheme.colorScheme.onTertiary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                singleLine = true,
                enabled = false
            )
            Text(
                stringResource(R.string.sex),
                Modifier.padding(top = 24.dp),
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge
            )
            Card(
                Modifier.padding(top = 12.dp),
                MaterialTheme.shapes.large,
                CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    state.list.forEachIndexed { index, it ->
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
            state.list != listOf(false, false, false)
                    && state.ageText != ""
        ) { callback?.onNext() }
    }
}

@Composable
private fun BottomSheetContent(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumberPicker(
            Modifier.padding(top = 40.dp),
            value = value,
            onValueChange = { onValueChange(it) },
            range = 18..100
        )
        GradientButton(
            Modifier.padding(top = 80.dp),
            stringResource(R.string.save_button), true
        ) { onSave() }
    }
}