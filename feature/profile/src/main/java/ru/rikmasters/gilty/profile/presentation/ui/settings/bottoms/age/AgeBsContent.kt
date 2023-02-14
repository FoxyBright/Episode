package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.age

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.BsContainer
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.NumberPicker

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
    val noMatter = stringResource(R.string.condition_no_matter)
    BsContainer(
        stringResource(R.string.personal_info_age_placeholder),
        modifier.fillMaxHeight(0.5f)
    ) {
        Column(
            Modifier.fillMaxSize(),
            SpaceBetween, CenterHorizontally
        ) {
            Spacer(modifier.width(1.dp))
            NumberPicker(
                Modifier, {
                    if(it == 17) noMatter
                    else it.toString()
                }, state.age, {
                    callback?.onAgeChange(it)
                }, range = state.range
            )
            GradientButton(
                Modifier.padding(bottom = 40.dp),
                stringResource(R.string.save_button)
            ) { callback?.onSave() }
        }
    }
}