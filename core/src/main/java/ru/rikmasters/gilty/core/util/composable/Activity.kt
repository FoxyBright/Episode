package ru.rikmasters.gilty.core.util.composable

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

val getActivity = @Composable {
    (LocalContext.current as Activity)
}

val getDensity = @Composable {
    LocalDensity.current
}

val getContext = @Composable {
    LocalContext.current
}

val getConfiguration = @Composable {
    LocalConfiguration.current
}