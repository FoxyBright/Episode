package ru.rikmasters.gilty.shared.shared

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldColors() = TextFieldDefaults.textFieldColors(
    textColor = MaterialTheme.colorScheme.tertiary,
    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
    unfocusedIndicatorColor = Color.Transparent,
    unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
    focusedIndicatorColor = Color.Transparent
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentTextFieldColors() = TextFieldDefaults.textFieldColors(
    textColor = MaterialTheme.colorScheme.tertiary,
    containerColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
    focusedIndicatorColor = Color.Transparent
)