package ru.rikmasters.gilty.presentation.ui.presentation.login

import android.content.res.Resources
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.core.visualTransformationOf
import ru.rikmasters.gilty.presentation.ui.theme.ExtraShapes
import ru.rikmasters.gilty.presentation.ui.theme.Shapes
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PhoneTextFieldPreview() {
    GiltyTheme() {

        var value by remember { mutableStateOf("") }

        PhoneTextField(
            value,
            Modifier.padding(32.dp)
        ) { value = it.trim() }
    }
}

private const val phoneMask = "### ###-##-##"
private val phoneMaxLength = phoneMask.count { it=='#' }
private val transformation = visualTransformationOf(phoneMask)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTextField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChanged: (String) -> Unit
) {
    TextField(
        value,
        { if(it.length <= phoneMaxLength) onValueChanged(it) },
        modifier,
        shape = MaterialTheme.shapes.large,
        colors = colors(),
        label = {
            Text(
                stringResource(R.string.phone_number),
                color = ThemeExtra.colors.secondaryTextColor
            )
        },

        trailingIcon = {
                       IconButton(onClick = {}) {
                           if (value.isNotEmpty()) {
                               Icon(
                                   painterResource(id = R.drawable.ic_close ),
                                   "clear",
                               tint = ThemeExtra.colors.grayIcon)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun colors() = TextFieldDefaults.textFieldColors(
    containerColor = ThemeExtra.colors.cardBackground,
    unfocusedIndicatorColor = Color.Transparent,
    unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
    focusedLabelColor = ThemeExtra.colors.mainTextColor,
    focusedIndicatorColor = Color.Transparent
)
