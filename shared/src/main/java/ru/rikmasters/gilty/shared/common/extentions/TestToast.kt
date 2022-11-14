package ru.rikmasters.gilty.shared.common.extentions

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Suppress("unused")
@Composable
fun toast(text: String) {
    Toast.makeText(LocalContext.current, text, Toast.LENGTH_SHORT).show()
}