package ru.rikmasters.gilty.core.util.composable

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun getActivity() = (LocalContext.current as Activity)