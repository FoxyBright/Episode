package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import ru.rikmasters.gilty.R

class Gradients() {
    @Composable
    fun green(): List<Color> {
        return listOf(
            colorResource(R.color.green_gradient_1),
            colorResource(R.color.green_gradient_2),
            colorResource(R.color.green_gradient_3)
        )
    }

    @Composable
    fun primary(): List<Color> {
        return listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
    }
}

