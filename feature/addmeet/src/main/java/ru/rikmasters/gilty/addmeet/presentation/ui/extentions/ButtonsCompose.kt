package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    online: Boolean,
    enabled: Boolean,
    activeDash: Int,
    onNext: () -> Unit,
) {
    Column(
        modifier
            .padding(bottom = 48.dp)
            .padding(horizontal = 16.dp),
        Center, CenterHorizontally
    ) {
        GradientButton(
            Modifier, stringResource(R.string.next_button),
            enabled, online
        ) { onNext() }
        Dashes(
            (4), activeDash,
            Modifier.padding(top = 16.dp),
            if(online) colorScheme.secondary
            else colorScheme.primary
        )
    }
}