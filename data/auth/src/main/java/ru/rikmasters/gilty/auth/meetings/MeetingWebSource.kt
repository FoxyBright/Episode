package ru.rikmasters.gilty.auth.meetings

import io.ktor.client.request.get
import io.ktor.client.request.post
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingWebSource: KtorSource() {
    
    suspend fun getCategoriesList(): List<CategoryModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/categories"
        ).wrapped<List<Category>>().map { it.map() }
    }
    
    suspend fun addMeet() {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/meetings"
        ) {
            //            setBody(
            //                MeetingRequest(
            //
            //                )
            //            )
        }
    }
}