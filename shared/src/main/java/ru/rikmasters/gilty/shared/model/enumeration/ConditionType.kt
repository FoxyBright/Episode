package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.*

enum class ConditionType {
    
    FREE,
    DIVIDE,
    ORGANIZER_PAY,
    MEMBER_PAY,
    NON_SELECT;
    
    companion object {
        
        val list = values().toList() - NON_SELECT
        
        fun get(index: Int) = list[index]
    }
    
    val display
        @Composable get() = stringResource(
            when(this) {
                FREE -> meeting_filter_select_meeting_type_free
                DIVIDE -> condition_divide
                ORGANIZER_PAY -> condition_organizer_pay
                MEMBER_PAY -> meeting_filter_select_meeting_type_paid
                NON_SELECT -> empty_String
            }
        )
}