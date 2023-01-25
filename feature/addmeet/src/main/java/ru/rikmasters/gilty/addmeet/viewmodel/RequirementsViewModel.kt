package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

var AgeFrom: String = ""
var AgeTo: String = ""
var Gender: Int? = null
var Orientation: String? = null
var MemberCount: String = "1"
var Private: Boolean = false

class RequirementsViewModel: ViewModel() {
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _hideMeetPlace = MutableStateFlow(Private)
    val hideMeetPlace = _hideMeetPlace.asStateFlow()
    
    private val _gender = MutableStateFlow(Gender)
    val gender = _gender.asStateFlow()
    
    private val _orientation = MutableStateFlow(Orientation)
    val orientation = _orientation.asStateFlow()
    
    private val _age = MutableStateFlow(Orientation)
    val age = _age.asStateFlow()
    
    private val _memberCount = MutableStateFlow(MemberCount)
    val memberCount = _memberCount.asStateFlow()
    suspend fun selectGender(gender: Int) {
        _gender.emit(gender)
        Gender = gender
    }
    
    suspend fun selectOrientation(orientation: String) {
        _orientation.emit(orientation)
        Orientation = orientation
    }
    
    suspend fun selectAge(age: Pair<String, String>) {
        _age.emit("от ${age.first} до ${age.second}")
        AgeFrom = age.first
        AgeTo = age.second
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun hideMeetPlace() {
        _hideMeetPlace.emit(!hideMeetPlace.value)
        Private = hideMeetPlace.value
    }
    
    suspend fun changeMemberCount(text: String) {
        //        val count = if(text.isNotBlank()) text.toInt() else 1
        //        when {
        //            count > selectedMember.size ->
        //                repeat(count) { selectedMember.add(false) }
        //
        //            count < selectedMember.size -> if(count > 1)
        //                repeat(selectedMember.size - count)
        //                { selectedMember.removeLast() }
        //
        //        }
        //        memberCount = text
        //        MEETING.memberCount = count
    }
}