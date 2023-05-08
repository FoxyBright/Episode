package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R

enum class MeetType {
    
    PERSONAL,
    GROUP,
    ANONYMOUS,
    NON_SELECT;
    
    companion object {
        
        val list = values().toList() - NON_SELECT
        fun get(index: Int) = list[index]
    }
    
    val display
        @Composable get() = stringResource(
            when(this) {
                PERSONAL -> R.string.meeting_personal_type
                ANONYMOUS -> R.string.meeting_anon_type
                GROUP -> R.string.meeting_group_type
                NON_SELECT -> R.string.empty_String
            }
        )
    
    val displayShort
        @Composable get() = stringResource(
            when(this) {
                PERSONAL -> R.string.meeting_filter_select_meeting_type_personal
                ANONYMOUS -> R.string.meeting_filter_select_meeting_type_anonymous
                GROUP -> R.string.meeting_filter_select_meeting_type_grouped
                NON_SELECT -> R.string.empty_String
            }
        )
}


