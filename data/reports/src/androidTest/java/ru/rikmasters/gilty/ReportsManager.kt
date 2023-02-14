package ru.rikmasters.gilty

import ru.rikmasters.gilty.shared.model.enumeration.ReportObjectType
import ru.rikmasters.gilty.shared.model.enumeration.ReportSubtype
import ru.rikmasters.gilty.shared.model.enumeration.ReportType

class ReportsManager(
    
    private val web: ReportsWebSource,
) {
    
    suspend fun sendReport(
        type: ReportType,
        subtype: ReportSubtype,
        description: String? = null,
        objectId: String,
        objectType: ReportObjectType,
    ) {
        web.sendReport(
            type, subtype, description,
            objectId, objectType
        )
    }
}