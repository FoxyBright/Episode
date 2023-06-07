package ru.rikmasters.gilty.shared.model

import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel


data class LastRespond(
    val image: String?,
    val isOnline: Boolean,
    val group: UserGroupTypeModel,
    val count: Int,
){
    companion object{
        val DemoLastRespond = LastRespond("", false, UserGroupTypeModel.DEFAULT, 0)
    }
}
