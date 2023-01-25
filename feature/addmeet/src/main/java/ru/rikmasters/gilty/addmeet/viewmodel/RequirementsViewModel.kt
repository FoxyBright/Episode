package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

var AgeFrom: String = "18"
var AgeTo: String = "18"
var Gender: Int? = null
var Orientation: String? = null
var MemberCount: String = ""
var Private: Boolean = false
var WithoutRespond: Boolean = false
var MemberLimited: Boolean = false

class RequirementsViewModel: ViewModel() {
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _withoutRespond = MutableStateFlow(WithoutRespond)
    val withoutRespond = _withoutRespond.asStateFlow()
    
    private val _memberLimited = MutableStateFlow(MemberLimited)
    val memberLimited = _memberLimited.asStateFlow()
    
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
    
    private val _tabs = MutableStateFlow(0)
    val tabs = _tabs.asStateFlow()
    
    private val _selectMember = MutableStateFlow(0)
    val selectMember = _selectMember.asStateFlow()
    
    suspend fun selectMember(member: Int) {
        _selectMember.emit(member)
    }
    
    suspend fun limitMembers() {
        _memberLimited.emit(!memberLimited.value)
        MemberLimited = memberLimited.value
    }
    
    suspend fun changeTab(tab: Int) {
        _tabs.emit(tab)
    }
    
    suspend fun withoutRespondChange() {
        _withoutRespond.emit(!withoutRespond.value)
        WithoutRespond = withoutRespond.value
    }
    
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
        _memberCount.emit(text)
        MemberCount = text
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