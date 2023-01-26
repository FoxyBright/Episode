package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

var AgeFrom: String = ""
var AgeTo: String = ""
var Gender: Int? = null
var Orientation: OrientationModel? = null
var MemberCount: String = ""
var Private: Boolean = false
var WithoutRespond: Boolean = false
var MemberLimited: Boolean = false
var Requirements = arrayListOf(
    RequirementModel(
        gender = "",
        ageMin = 0,
        ageMax = 0,
        orientation = null
    )
)
var RequirementsType: Int = 0

class RequirementsViewModel: ViewModel() {
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _withoutRespond = MutableStateFlow(WithoutRespond)
    val withoutRespond = _withoutRespond.asStateFlow()
    
    private val _memberLimited = MutableStateFlow(MemberLimited)
    val memberLimited = _memberLimited.asStateFlow()
    
    private val _private = MutableStateFlow(Private)
    val private = _private.asStateFlow()
    
    private val _gender = MutableStateFlow(Gender)
    val gender = _gender.asStateFlow()
    
    private val _orientation = MutableStateFlow(Orientation)
    val orientation = _orientation.asStateFlow()
    
    private val _requirements = MutableStateFlow(Requirements)
    val requirements = _requirements.asStateFlow()
    
    private val _age = MutableStateFlow(
        if(AgeFrom.isBlank() || AgeTo.isBlank())
            "" else "от $AgeFrom до $AgeTo"
    )
    val age = _age.asStateFlow()
    
    private val _memberCount = MutableStateFlow(MemberCount)
    val memberCount = _memberCount.asStateFlow()
    
    private val _tabs = MutableStateFlow(0)
    val tabs = _tabs.asStateFlow()
    
    private val _selectMember = MutableStateFlow(0)
    val selectMember = _selectMember.asStateFlow()
    
    suspend fun selectMember(member: Int) {
        _selectMember.emit(member)
        AgeFrom = ""
        AgeTo = ""
        Gender = null
        Orientation = null
        val req = Requirements[member]
        _age.emit(
            if(req.ageMin == 0 || req.ageMax == 0) ""
            else "от ${req.ageMin} до ${req.ageMax}"
        )
        _gender.emit(
            try {
                GenderType.valueOf(req.gender).ordinal
            } catch(e: Exception) {
                null
            }
        )
        _orientation.emit(req.orientation)
    }
    
    private suspend fun updateRequirements() {
        _requirements.emit(Requirements)
    }
    
    suspend fun limitMembers() {
        _memberLimited.emit(!memberLimited.value)
        MemberLimited = memberLimited.value
    }
    
    suspend fun changeTab(tab: Int) {
        _tabs.emit(tab)
        RequirementsType = tab
    }
    
    suspend fun withoutRespondChange() {
        _withoutRespond.emit(!withoutRespond.value)
        WithoutRespond = withoutRespond.value
    }
    
    suspend fun selectGender(gender: Int) {
        _gender.emit(gender)
        Gender = gender
        Requirements[selectMember.value] =
            Requirements[selectMember.value].copy(
                gender = Gender?.let {
                    GenderType.get(it).name
                } ?: ""
            )
        updateRequirements()
    }
    
    suspend fun selectOrientation(orientation: OrientationModel) {
        _orientation.emit(orientation)
        Orientation = orientation
        Requirements[selectMember.value] =
            Requirements[selectMember.value].copy(
                orientation = Orientation
            )
        updateRequirements()
    }
    
    suspend fun selectAge(age: Pair<String, String>) {
        _age.emit("от ${age.first} до ${age.second}")
        AgeFrom = age.first
        AgeTo = age.second
        val ageMin = try {
            AgeFrom.toInt()
        } catch(e: Exception) {
            0
        }
        val ageMax = try {
            AgeTo.toInt()
        } catch(e: Exception) {
            0
        }
        Requirements[selectMember.value] =
            Requirements[selectMember.value].copy(
                ageMin = ageMin, ageMax = ageMax
            )
        updateRequirements()
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changePrivate() {
        _private.emit(!private.value)
        Private = private.value
    }
    
    suspend fun changeMemberCount(text: String) {
        if(text.isNotBlank()
            && text.toInt() > 1000
        ) return
        
        _memberCount.emit(text)
        MemberCount = text
        if(text.isBlank() || text.toInt() < 2) {
            _tabs.emit(0)
            _selectMember.emit(0)
        }
        
        try {
            val count = memberCount.value.toInt()
            val size = Requirements.size
            when {
                size < count -> {
                    repeat(count.minus(size)) {
                        Requirements.add(
                            RequirementModel(
                                gender = "",
                                ageMin = 0,
                                ageMax = 0,
                                orientation = null
                            )
                        )
                    }
                }
                
                size > count -> {
                    repeat(size.minus(count)) {
                        Requirements.removeLast()
                    }
                }
            }
        } catch(e: Exception) {
            val req = Requirements.first()
            Requirements.clear()
            Requirements.add(req)
        }
        updateRequirements()
    }
    
    suspend fun clearCount() {
        _memberCount.emit("")
        MemberCount = ""
    }
}