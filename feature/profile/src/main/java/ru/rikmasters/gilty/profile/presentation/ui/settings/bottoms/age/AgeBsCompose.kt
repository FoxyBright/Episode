package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.age

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ListItemPicker

data class AgeBsState(
    val range: IntRange,
    val age: Int,
)

interface AgeBsCallback {
    
    fun onAgeChange(age: Int)
    fun onSave()
}

@Composable
fun AgeBsContent(
    state: AgeBsState,
    modifier: Modifier = Modifier,
    callback: AgeBsCallback? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.55f)
            .background(colorScheme.background)
            .padding(16.dp)
            .padding(top = 10.dp)
    ) {
        val noMatter = stringResource(R.string.condition_no_matter)
        Box(
            Modifier
                .align(TopCenter)
                .fillMaxHeight(0.8f)
        ) {
            ListItemPicker(
                value = state.age,
                list = state.range.toList(),
                modifier = Modifier,
                label = { if(it == 17) noMatter else "$it" },
            ) { callback?.onAgeChange(it) }
        }
        Text(
            stringResource(R.string.personal_info_age_placeholder),
            Modifier, colorScheme.tertiary,
            style = typography.labelLarge,
        )
        GradientButton(
            Modifier
                .align(BottomCenter)
                .padding(bottom = 32.dp),
            stringResource(R.string.save_button),
        ) { callback?.onSave() }
    }
}