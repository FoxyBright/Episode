package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.GROUP
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

var AgeFrom: String = ""
var AgeTo: String = ""
var Gender: Int? = null
var Orientation: OrientationModel? = null
var Requirements = arrayListOf(
    RequirementModel(
        gender = null,
        ageMin = 0,
        ageMax = 0,
        orientation = null
    )
)
var RequirementsType = 0

class RequirementsViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _withoutRespond = MutableStateFlow(false)
    val withoutRespond = _withoutRespond.asStateFlow()
    
    private val _limited = MutableStateFlow(false)
    val limited = _limited.asStateFlow()
    
    private val _private = MutableStateFlow(false)
    val private = _private.asStateFlow()
    
    private val _gender = MutableStateFlow<Int?>(null)
    val gender = _gender.asStateFlow()
    
    private val _orientation = MutableStateFlow<OrientationModel?>(null)
    val orientation = _orientation.asStateFlow()
    
    private val _requirements = MutableStateFlow(emptyList<RequirementModel>())
    val requirements = _requirements.asStateFlow()
    
    private val _age = MutableStateFlow("")
    val age = _age.asStateFlow()
    
    private val _memberCount = MutableStateFlow("")
    val memberCount = _memberCount.asStateFlow()
    
    private val _tabs = MutableStateFlow(0)
    val tabs = _tabs.asStateFlow()
    
    private val _selectMember = MutableStateFlow(0)
    val selectMember = _selectMember.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    private val _meetType = MutableStateFlow(GROUP)
    val meetType = _meetType.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest { add ->
                _memberCount.emit(add?.memberCount ?: "")
                _requirements.emit(
                    add?.requirements?.let { reqList ->
                        reqList.firstOrNull()?.let { req ->
                            _age.emit(
                                req.ageMin?.let { min ->
                                    req.ageMax?.let { max ->
                                        if(min == 0 || max == 0) ""
                                        else "от $min до $max"
                                    }
                                } ?: ""
                            )
                            _gender.emit(
                                try {
                                    GenderType.valueOf(
                                        req.gender?.name.toString()
                                    ).ordinal
                                } catch(e: Exception) {
                                    null
                                }
                            )
                        }
                        reqList
                    } ?: emptyList()
                )
                _private.emit(add?.isPrivate ?: false)
                _limited.emit(add?.memberLimited ?: false)
                _withoutRespond.emit(add?.withoutResponds ?: false)
                _online.emit(add?.isOnline ?: false)
                _meetType.emit(add?.type ?: GROUP)
            }
        }
    }
    
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
                GenderType.valueOf(req.gender?.name.toString()).ordinal
            } catch(e: Exception) {
                null
            }
        )
        _orientation.emit(req.orientation)
    }
    
    suspend fun limitMembers() {
        _limited.emit(!limited.value)
        manager.update(memberLimited = limited.value)
    }
    
    suspend fun changeTab(tab: Int) {
        _tabs.emit(tab)
        RequirementsType = tab
    }
    
    suspend fun withoutRespondChange() {
        _withoutRespond.emit(!withoutRespond.value)
        manager.update(withoutResponds = withoutRespond.value)
    }
    
    suspend fun selectGender(gender: Int) {
        _gender.emit(gender)
        Gender = gender
        Requirements[selectMember.value] =
            Requirements[selectMember.value].copy(
                gender = try {
                    Gender?.let { GenderType.get(it) }
                } catch(e: Exception) {
                    null
                }
            )
        _requirements.emit(Requirements)
    }
    
    suspend fun selectOrientation(orientation: OrientationModel) {
        _orientation.emit(orientation)
        Orientation = orientation
        Requirements[selectMember.value] =
            Requirements[selectMember.value].copy(
                orientation = Orientation
            )
        _requirements.emit(Requirements)
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
        _requirements.emit(Requirements)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changePrivate() {
        _private.emit(!private.value)
        manager.update(isPrivate = private.value)
    }
    
    suspend fun changeMemberCount(text: String) {
        if(text.isNotBlank()
            && text.toInt() > 1000
        ) return
        
        _memberCount.emit(text)
        manager.update(memberCount = text)
        
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
                        Requirements.add(RequirementModel())
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
        _requirements.emit(Requirements)
    }
    
    suspend fun setRequirements() {
        manager.update(
            requirementsType = if(RequirementsType == 0)
                "ALL" else "EACH",
            requirements = Requirements,
        )
    }
    
    suspend fun clearCount() {
        _memberCount.emit("")
        manager.update(memberCount = "")
    }
}