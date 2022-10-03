package ru.rikmasters.gilty.presentation.ui.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.core.NumberPicker
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun AgeBottomSheetPreview() {
    GiltyTheme {
        AgeBottomSheetCompose()
    }
}
@Composable
fun AgeBottomSheetCompose() {

    Box(Modifier.wrapContentHeight()) {

        var pickerValue by remember { mutableStateOf(18) }
        NumberPicker(
            modifier = Modifier
                .align(Alignment.TopCenter).padding(bottom = 122.dp),
            value = pickerValue,
            onValueChange = { pickerValue= it },
            range = 18..100 )

        GradientButton(onClick = { /*TODO*/ },
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            text = stringResource(id = R.string.save_button), enabled = true)
    }
}