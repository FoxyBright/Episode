package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.shared.GradientButton

@Composable
fun PhoneField(
    modifier: Modifier = Modifier,
    phone: String,
    country: Country,
    callback: LoginCallback? = null
) {
    Box(
        modifier.clip(shapes.extraSmall)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorScheme.onPrimaryContainer),
            Start, CenterVertically
        ) {
            Image(
                country.flag,
                stringResource(R.string.select_country),
                Modifier
                    .padding(start = 10.dp)
                    .size(20.dp)
                    .clickable
                    { callback?.changeCountry() }
            )
            PhoneTextField(
                phone,
                country,
                Modifier.fillMaxWidth(),
                { callback?.onClear() },
                { callback?.onPhoneChange(it) }
            )
        }
    }
}

@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onClick: () -> Unit
) {
    GradientButton(
        modifier.padding(top = 20.dp),
        stringResource(R.string.next_button),
        isActive,
        onClick = onClick
    )
}