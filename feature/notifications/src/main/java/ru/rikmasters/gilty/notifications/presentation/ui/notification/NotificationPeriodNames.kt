package ru.rikmasters.gilty.notifications.presentation.ui.notification

import ru.rikmasters.gilty.shared.R

enum class NotificationPeriodNames(
    private val monthNumber: Int,
    private val value: Int,
) {
    
    JANUARY(1, R.string.month_january_name),
    FEBRUARY(2, R.string.month_february_name),
    MARCH(3, R.string.month_march_name),
    APRIL(4, R.string.month_april_name),
    MAY(5, R.string.month_may_name),
    JUNE(6, R.string.month_june_name),
    JULY(7, R.string.month_july_name),
    AUGUST(8, R.string.month_august_name),
    SEPTEMBER(9, R.string.month_september_name),
    OCTOBER(10, R.string.month_october_name),
    NOVEMBER(11, R.string.month_november_name),
    DECEMBER(12, R.string.month_december_name),
    
    TODAY(20, R.string.meeting_profile_bottom_today_label),
    YESTERDAY(30, R.string.meeting_profile_bottom_yesterday_label),
    THIS_WEEK(40, R.string.notification_on_this_week_label),
    THIS_MONTH(50, R.string.meeting_profile_bottom_30_days_earlier_label),
    LAST(0, R.string.meeting_profile_bottom_latest_label);
    
    companion object {
        
        fun getPeriodName(monthNumber: Int): Int {
            values().forEach {
                if(it.monthNumber == monthNumber)
                    return it.value
            }
            return LAST.value
        }
    }
}