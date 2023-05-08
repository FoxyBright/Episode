package ru.rikmasters.gilty.shared.model.report

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.MEETING
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.PHOTO
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.PROFILE

enum class ReportSubtype {
    MEETING_SUSPICIOUS_PLACE,
    MEETING_WRONG_CATEGORY,
    PHOTO_ANOTHER_USER,
    INTRUSIVE_MAILING,
    ADVERTISEMENT,
    PHOTO_FAMOUS,
    MISLEADING,
    DECEPTION,
    FRAUD,
    OTHER,
    
    // to Mapping
    FALSE_INFORMATION,
    SPAM,
    SCAM;
    
    companion object {
        
        fun get(
            reportObject: ReportObjectType,
            report: Report,
        ) = when(report) {
            is FalseInformation -> when(reportObject) {
                MEETING -> listOf(
                    PHOTO_ANOTHER_USER,
                    PHOTO_FAMOUS,
                    MEETING_WRONG_CATEGORY,
                    MEETING_SUSPICIOUS_PLACE,
                    OTHER
                )
                
                PROFILE -> listOf(
                    PHOTO_ANOTHER_USER,
                    PHOTO_FAMOUS,
                    OTHER
                )
                
                PHOTO -> emptyList()
            }
            
            is Other -> listOf(OTHER)
            
            is Spam -> listOf(
                ADVERTISEMENT,
                INTRUSIVE_MAILING
            )
            
            is Scam -> listOf(
                FRAUD,
                DECEPTION,
                MISLEADING
            )
        }
    }
    
    fun report(objectType: ReportObjectType) = when(this) {
        FALSE_INFORMATION -> FalseInformation(objectType)
        SPAM -> Spam(objectType)
        SCAM -> Scam(objectType)
        OTHER -> Other(objectType)
        else -> throw Throwable("В навигацию попал не определенный класс")
    }
    
    val display
        @Composable get() = stringResource(
            when(this) {
                MEETING_SUSPICIOUS_PLACE -> R.string.complaints_lie_suspicious_label
                MEETING_WRONG_CATEGORY -> R.string.complaints_lie_bad_category_label
                PHOTO_ANOTHER_USER -> R.string.complaints_lie_anything_photo_label
                INTRUSIVE_MAILING -> R.string.complaints_spam_sending_label
                ADVERTISEMENT -> R.string.complaints_spam_advertisement_label
                PHOTO_FAMOUS -> R.string.complaints_lie_celebrity_label
                MISLEADING -> R.string.complaints_cheater_delusion_label
                DECEPTION -> R.string.complaints_cheater_deception_label
                FRAUD -> R.string.complaints_cheater_fraud_label
                OTHER -> R.string.complaints_other
                
                // to mapping
                FALSE_INFORMATION -> R.string.complaints_lie_title
                SPAM -> R.string.complaints_spam_title
                SCAM -> R.string.complaints_cheater_title
            }
        )
}