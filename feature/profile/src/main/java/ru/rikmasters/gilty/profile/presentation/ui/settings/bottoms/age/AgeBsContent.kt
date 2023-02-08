package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.age

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopCenter
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
    BsContainer(
        stringResource(R.string.personal_info_age_placeholder),
        modifier.fillMaxHeight(0.6f)
    ) {
        Box(Modifier.fillMaxSize(), TopCenter) {
            NumberPicker(
                Modifier.padding(top = 40.dp),
                value = state.age, onValueChange = {
                    callback?.onAgeChange(it)
                }, range = state.range
            )
            GradientButton(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                stringResource(R.string.save_button)
            ) { callback?.onSave() }
        }
    }
}