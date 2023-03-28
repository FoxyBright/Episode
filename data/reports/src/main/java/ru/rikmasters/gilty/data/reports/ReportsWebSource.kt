package ru.rikmasters.gilty.data.reports

import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL

class ReportsWebSource: KtorSource() {
    
    suspend fun sendReport(report: ReportRequest) {
        post(
            "http://$HOST$PREFIX_URL/reports"
        ) { setBody(report) }
    }
}