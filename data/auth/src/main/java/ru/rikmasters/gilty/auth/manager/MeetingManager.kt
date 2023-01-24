package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.meetings.MeetingWebSource
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class MeetingManager(
    
    private val web: MeetingWebSource,
) {
    
    suspend fun getCategoriesList(): List<CategoryModel> =
        web.getCategoriesList()
    
    suspend fun addMeet() {
        web.addMeet()
    }
}