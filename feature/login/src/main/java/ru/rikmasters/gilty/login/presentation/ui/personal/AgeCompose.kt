package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
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
    onSave: () -> Unit
) {
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45f)
            .padding(16.dp),
        TopCenter
    ) {
        NumberPicker(
            Modifier.padding(top = 40.dp),
            value = value ?: range.first,
            onValueChange = { onValueChange(it) },
            range = range
        )
        GradientButton(
            Modifier
                .align(BottomCenter)
                .padding(bottom = 40.dp),
            stringResource(R.string.save_button)
        ) { onSave() }
    }
}