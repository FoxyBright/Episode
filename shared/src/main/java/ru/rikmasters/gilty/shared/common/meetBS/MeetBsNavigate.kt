package ru.rikmasters.gilty.shared.common.meetBS

enum class MeetNavigation {
    PARTICIPANTS,
    COMPLAINTS,
    ORGANIZER,
    MEET
}

data class Navigator(
    val navigation: MeetNavigation,
    val parameters: String
)