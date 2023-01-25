package ru.rikmasters.gilty.shared.model.enumeration

enum class GenderType(val value: String, val EN: String) {
    
    FEMALE("Женский", "woman"),
    MALE("Мужской", "man"),
    OTHER("Другое", "other");
    
    companion object {
        
        val list = values().toList()
        fun get(index: Int) = list[index]
    }
}