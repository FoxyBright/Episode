package ru.rikmasters.gilty.shared.models.enumeration

enum class UserGroupTypeDTO {
    DEFAULT, PREMIUM, TEAM, ORIGINAL, PARTNER;

    fun map() = when (this) {
        DEFAULT -> UserGroupTypeModel.DEFAULT
        PREMIUM -> UserGroupTypeModel.PREMIUM
        TEAM -> UserGroupTypeModel.TEAM
        ORIGINAL -> UserGroupTypeModel.ORIGINAL
        PARTNER -> UserGroupTypeModel.PARTNER
    }
}