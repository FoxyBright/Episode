package ru.rikmasters.gilty.data.reports

data class ReportRequest(
    val type: String?,
    val subtype: String?,
    val description: String?,
    val objectId: String?,
    val objectType: String?,
)