package ru.rikmasters.gilty.shared.model.enumeration

enum class MeetFilterGroupType(val value: String) {
    AFTER("after"),
    TODAY("today");
    
    companion object {
        
        fun get(index: Int) = values()[index]
    }
}