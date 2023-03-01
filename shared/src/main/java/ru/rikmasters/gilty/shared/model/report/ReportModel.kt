package ru.rikmasters.gilty.shared.model.report

data class ReportModel(
    val type: Report?,
    val subtype: ReportSubtype?,
    val description: String?,
    val objectId: String?,
    val objectType: ReportObjectType?,
)