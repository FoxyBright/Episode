package ru.rikmasters.gilty.shared.model.enumeration

enum class GenderType(val value: String) {
    
    FEMALE("Женский"),
    MALE("Мужской"),
    OTHER("Другое"),
    NOT_IMPORTANT("Неважно");
    
    companion object {
        
        val fullList = values().toList()
        val shortList = values().toList() - NOT_IMPORTANT
        fun get(index: Int) = fullList[index]
    }
}