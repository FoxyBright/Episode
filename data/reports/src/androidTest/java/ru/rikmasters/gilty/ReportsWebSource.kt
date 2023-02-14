package ru.rikmasters.gilty

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.enumeration.ReportObjectType
import ru.rikmasters.gilty.shared.model.enumeration.ReportSubtype
import ru.rikmasters.gilty.shared.model.enumeration.ReportType

class ReportsWebSource: KtorSource() {
    
    suspend fun sendReport(
        type: ReportType,
        subtype: ReportSubtype,
        description: String?,
        objectId: String,
        objectType: ReportObjectType,
    ) {
        updateClientToken()
        client.post(
            "http:/$HOST$PREFIX_URL/reports"
        ) {
            setBody(
                ReportRequest(
                    type.name,
                    subtype.name,
                    description,
                    objectId,
                    objectType.name
                )
            )
        }
    }
}