package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.meeting_anon_type
import ru.rikmasters.gilty.shared.R.string.meeting_group_type
import ru.rikmasters.gilty.shared.R.string.meeting_personal_type
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.GROUP
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.PERSONAL

enum class MeetType {
    
    PERSONAL, ANONYMOUS, GROUP;
    
    val display
        @Composable get() = stringResource(
            when(this) {
                PERSONAL -> meeting_personal_type
                ANONYMOUS -> meeting_anon_type
                GROUP -> meeting_group_type
            }
        )
}

fun getMeetType(name: String) = when(name) {
    "PERSONAL" -> PERSONAL
    "ANONYMOUS" -> ANONYMOUS
    else -> GROUP
}


