package ru.rikmasters.gilty.shared.model.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.*

data class OrientationModel(
    val id: String,
    val name: String,
) {
    
    val display
        @Composable get() = stringResource(
            when(id) {
                "DEMISEXUAL" -> orientation_demisexual
                "PANSEXUAL" -> orientation_pansexual
                "BISEXUAL" -> orientation_bisexual
                "ASEXUAL" -> orientation_asexual
                "LESBIAN" -> orientation_lesbian
                "QUEER" -> orientation_queer
                "GAY" -> orientation_gay
                else -> orientation_hetero
            }
        )
}

val DemoOrientationModel = OrientationModel(
    "0", "HETERO"
)