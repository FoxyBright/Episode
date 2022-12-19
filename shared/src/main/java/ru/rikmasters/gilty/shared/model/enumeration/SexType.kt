package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.*

enum class SexType() {
    
    FEMALE, MALE, OTHER;
    
    val display
        @Composable get() =
            stringResource(
                when(this) {
                    FEMALE -> female_sex
                    OTHER -> others_sex
                    MALE -> male_sex
                }
            )
    
    val displayEN
        @Composable get() =
            stringResource(
                when(this) {
                    FEMALE -> female_sex_en
                    OTHER -> others_sex_en
                    MALE -> male_sex_en
                }
            )
}