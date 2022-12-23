package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.*

enum class GenderType() {
    
    DEMISEXUAL, PANSEXUAL,
    BISEXUAL, ASEXUAL, LESBIAN,
    HETERO, QUEER, GAY;
    
    val display
        @Composable get() = stringResource(
            when(this) {
                DEMISEXUAL -> orientation_demisexual
                PANSEXUAL -> orientation_pansexual
                BISEXUAL -> orientation_bisexual
                ASEXUAL -> orientation_asexual
                LESBIAN -> orientation_lesbian
                HETERO -> orientation_hetero
                QUEER -> orientation_queer
                GAY -> orientation_gay
            }
        )
}