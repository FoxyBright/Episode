package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun PersonalInfoPreview() {
    GiltyTheme {
        PersonalContent(
            PersonalState((18), (2)),
            Modifier.background(colorScheme.background)
        )
    }
}

interface PersonalCallback {
    
    fun onBack()
    fun onNext()
    fun onGenderChange(index: Int)
    fun onAgeClick()
}

data class PersonalState(
    val age: Int? = null,
    val selectGender: Int? = null
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PersonalContent(
    state: PersonalState,
    modifier: Modifier = Modifier,
    callback: PersonalCallback? = null,
) {
    Box(modifier.fillMaxSize()) {
        Column {
            ActionBar(stringResource(R.string.personal_info_title))
            { callback?.onBack() }
            Text(
                stringResource(R.string.how_old_are_you),
                Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp),
                colorScheme.tertiary,
                style = typography.titleLarge
            )
            Card(
                { callback?.onAgeClick() }, Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = shapes.large,
                colors = cardColors(colorScheme.primaryContainer)
            ) {
                Text(
                    "${state.age ?: stringResource(R.string.personal_info_age_placeholder)}",
                    Modifier.padding(16.dp),
                    if(state.age == null)
                        colorScheme.onTertiary
                    else colorScheme.tertiary,
                    style = typography.bodyMedium,
                    fontWeight = Bold
                )
            }
            Text(
                stringResource(R.string.sex),
                Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp),
                colorScheme.tertiary,
                style = typography.titleLarge
            )
            Card(
                Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp),
                shapes.large,
                cardColors(colorScheme.primaryContainer)
            ) {
                val sexList = GenderType.values().toList()
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(sexList.size) {
                        GiltyChip(
                            Modifier.padding(end = 12.dp),
                            sexList[it].value,
                            (state.selectGender == it)
                        ) { callback?.onGenderChange(it) }
                    }
                }
            }
        }
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(BottomCenter),
            stringResource(R.string.next_button), (true)
        ) { callback?.onNext() }
    }
}

