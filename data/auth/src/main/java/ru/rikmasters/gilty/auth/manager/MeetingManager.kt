package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.meetings.MeetingWebSource
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class MeetingManager(
    
    private val web: MeetingWebSource,
) {
    
    suspend fun getPopularTags(list: List<String?>) =
        web.getPopularTags(list)
    
    suspend fun getLastPlaces() =
        web.getLastPlaces()
    
    suspend fun getOrientations() =
        web.getOrientations()
    
    suspend fun searchTags(tag: String) =
        web.searchTags(tag)
    
    suspend fun addNewTag(tag: String) =
        web.addNewTag(tag)
    
    suspend fun getCategoriesList(): List<CategoryModel> =
        web.getCategoriesList()
    
    suspend fun addMeet() {
        web.addMeet()
    }
}