package ru.rikmasters.gilty.data.reports

import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.wrapper.coroutinesState

class ReportsWebSource: KtorSource() {
    
    suspend fun sendReport(report: ReportRequest) =
        tryPost("http://$HOST$PREFIX_URL/reports") {
            setBody(report)
        }.let { coroutinesState({ it }) {} }
}