package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.NumberPicker

@Composable
fun AgeBottomSheetContent(
    value: Int?,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
) {
    val noMatter = stringResource(R.string.condition_no_matter)
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(16.dp),
        TopCenter
    ) {
        Column(
            Modifier.fillMaxSize(),
            SpaceBetween, CenterHorizontally
        ) {
            Spacer(modifier.width(1.dp))
            NumberPicker(
                Modifier.padding(horizontal = 16.dp), {
                    if(it == 17) noMatter
                    else it.toString()
                }, value ?: range.first, {
                    onValueChange(it)
                }, range = range
            )
            GradientButton(
                Modifier.padding(bottom = 40.dp),
                stringResource(R.string.save_button)
            ) { onSave() }
        }
    }
}