package ru.rikmasters.gilty.shared.models.enums

enum class MeetFilterGroup(val value: String) {
    AFTER("after"),
    TODAY("today");
    
    companion object {
        
        fun get(index: Int) = values()[index]
    }
}