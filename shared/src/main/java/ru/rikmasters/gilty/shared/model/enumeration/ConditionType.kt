package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.*

enum class ConditionType {
    
    ORGANIZER_PAY, MEMBER_PAY,
    DIVIDE, FREE, NO_MATTER;
    
    val display
        @Composable get() = stringResource(
            when(this) {
                ORGANIZER_PAY -> condition_organizer_pay
                MEMBER_PAY -> condition_member_pay
                DIVIDE -> condition_divide
                FREE -> condition_free
                NO_MATTER -> condition_no_matter
            }
        )
}