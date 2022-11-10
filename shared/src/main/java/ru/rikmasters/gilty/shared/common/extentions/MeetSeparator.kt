package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.runtime.Composable
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel

@Composable
fun MeetSeparate(
    responds: List<RespondModel>
): List<Pair<MeetingModel, List<RespondModel>>> {
    val meetList = arrayListOf<MeetingModel>()
    val list =
        arrayListOf<Pair<MeetingModel, List<RespondModel>>>()
    responds.forEach { if (!meetList.contains(it.meet)) meetList.add(it.meet) }
    meetList.forEach { meet ->
        val resList = arrayListOf<RespondModel>()
        responds.forEach { if (!resList.contains(it)) resList.add(it) }
        list.add(Pair(meet, resList))
    }; return list
}