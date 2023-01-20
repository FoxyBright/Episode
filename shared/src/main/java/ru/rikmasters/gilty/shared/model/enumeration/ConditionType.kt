package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.*
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.*

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

fun getConditionType(name: String) = when(name) {
    "ORGANIZER_PAY" -> ORGANIZER_PAY
    "MEMBER_PAY" -> MEMBER_PAY
    "DIVIDE" -> DIVIDE
    "FREE" -> FREE
    else -> NO_MATTER
}