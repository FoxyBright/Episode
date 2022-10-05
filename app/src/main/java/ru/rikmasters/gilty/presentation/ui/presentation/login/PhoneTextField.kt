package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.core.visualTransformationOf
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PhoneTextFieldPreview() {
    GiltyTheme {
        PhoneTextField(
            "9105152312",
            "+7",
            Modifier.padding(32.dp)
        )
    }
}

private var regionMask = "+7"
private val phoneMask = "$regionMask ### ###-##-##"
private val phoneMaxLength = phoneMask.count { it == '#' }
private val transformation = visualTransformationOf(phoneMask)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTextField(
    value: String,
    region: String,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
    onValueChanged: ((String) -> Unit)? = null
) {
    regionMask = region
    TextField(
        value,
        {
            if (it.length <= phoneMaxLength)
                if (onValueChanged != null)
                    onValueChanged(it)
        },
        modifier,
        shape = MaterialTheme.shapes.large,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = ThemeExtra.colors.cardBackground,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
            focusedLabelColor = ThemeExtra.colors.mainTextColor,
            focusedIndicatorColor = Color.Transparent
        ),
        label = {
            Text(
                stringResource(R.string.phone_number),
                color = ThemeExtra.colors.secondaryTextColor
            )
        },
        trailingIcon = {
            IconButton({ if (onClear != null) onClear() }) {
                if (value.isNotEmpty()) {
                    Icon(
                        painterResource(R.drawable.ic_close),
                        stringResource(R.string.clear),
                        tint = ThemeExtra.colors.grayIcon
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        ),
        visualTransformation = transformation
    )
}
