package ru.rikmasters.gilty.shared.model.report

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.report.ReportSubtype.*

sealed interface Report {
    
    companion object {
        
        fun all(type: ReportObjectType) =
            listOf(
                FalseInformation(type),
                Spam(type), Scam(type),
                Other(type),
            )
    }
    
    @Composable
    fun display(): String
    
    fun map(): ReportSubtype
    
    val name: String
    
    val subTypes: List<ReportSubtype>
}

class FalseInformation(reportObject: ReportObjectType): Report {
    
    override fun map() = FALSE_INFORMATION
    
    override val name = map().name
    
    @Composable
    override fun display() = stringResource(R.string.complaints_lie_title)
    
    override val subTypes: List<ReportSubtype> =
        ReportSubtype.get(reportObject, this)
}

class Spam(reportObject: ReportObjectType): Report {
    
    override fun map() = SPAM
    
    override val name = map().name
    
    @Composable
    override fun display() = stringResource(R.string.complaints_spam_title)
    
    override val subTypes: List<ReportSubtype> =
        ReportSubtype.get(reportObject, this)
}

class Scam(reportObject: ReportObjectType): Report {
    
    override fun map() = SCAM
    
    override val name = map().name
    
    @Composable
    override fun display() = stringResource(R.string.complaints_cheater_title)
    
    override val subTypes: List<ReportSubtype> =
        ReportSubtype.get(reportObject, this)
}

class Other(reportObject: ReportObjectType): Report {
    
    override fun map() = OTHER
    
    override val name = map().name
    
    @Composable
    override fun display() = stringResource(R.string.complaints_other)
    
    override val subTypes: List<ReportSubtype> =
        ReportSubtype.get(reportObject, this)
}