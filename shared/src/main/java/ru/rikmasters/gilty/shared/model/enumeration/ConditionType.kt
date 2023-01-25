package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.*

enum class ConditionType {
    
    FREE,
    DIVIDE,
    ORGANIZER_PAY,
    MEMBER_PAY,
    NO_MATTER;
    
    companion object {
        
        val list = values().toList()
        
        fun get(index: Int) = list[index]
    }
    
    val display
        @Composable get() = stringResource(
            when(this) {
                ORGANIZER_PAY -> condition_organizer_pay
                MEMBER_PAY -> meeting_filter_select_meeting_type_paid
                DIVIDE -> condition_divide
                FREE -> meeting_filter_select_meeting_type_free
                NO_MATTER -> condition_no_matter
            }
        )
}