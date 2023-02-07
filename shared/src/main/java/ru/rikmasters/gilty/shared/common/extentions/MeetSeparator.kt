package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.runtime.Composable
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.notification.ShortRespondModel

@Composable
fun meetSeparate(
    responds: List<ShortRespondModel>
): List<Pair<MeetingModel, List<ShortRespondModel>>> {
    val meetList = arrayListOf<MeetingModel>()
    val list =
        arrayListOf<Pair<MeetingModel, List<ShortRespondModel>>>()
    responds.forEach { if (!meetList.contains(it.meet)) meetList.add(it.meet) }
    meetList.forEach { meet ->
        val resList = arrayListOf<ShortRespondModel>()
        responds.forEach { if (!resList.contains(it)) resList.add(it) }
        list.add(Pair(meet, resList))
    }; return list
}