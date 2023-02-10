package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.addmeet.viewmodel.CompleteViewModel.RequirementType.Companion.get
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.extentions.durationToMinutes
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import java.util.UUID.randomUUID

class CompleteViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _meet = MutableStateFlow<MeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private enum class RequirementType {
        ALL, EACH;
        
        companion object {
            
            fun get(index: Int) = values()[index]
        }
    }
    
    suspend fun setMeet() {
        val meet = MeetingModel(
            randomUUID().toString(),
            Tags.joinToString(separator = ", ") { it.title },
            Condition!!, SelectCategory!!, Duration, MeetingType!!, Date,
            profileManager.getProfile().map(), Online,
            Tags, Description, Private, try {
                MemberCount.toInt()
            } catch(e: Exception) {
                1
            }, (null), Place, Address, HideAddress, try {
                Price.toInt()
            } catch(e: Exception) {
                0
            }
        )
        _meet.emit(meet)
    }
    
    suspend fun addMeet() {
        val id = meetManager.addMeet(
            categoryId = SelectCategory?.id,
            type = MeetingType?.name,
            isOnline = Online,
            condition = Condition?.name,
            price = if(Condition == ConditionType.MEMBER_PAY) try {
                Price.toInt()
            } catch(e: Exception) {
                null
            } else null,
            photoAccess = Hidden,
            chatForbidden = RestrictChat,
            tags = Tags.map { it.title },
            description = Description.ifBlank { null },
            dateTime = Date,
            duration = durationToMinutes(Duration),
            hide = HideAddress,
            lat = (0),
            lng = (0),
            place = Place,
            address = Address,
            isPrivate = Private,
            memberCount = if(Private) 0 else try {
                MemberCount.toInt()
            } catch(e: Exception) {
                0
            },
            requirementsType = get(RequirementsType).name,
            requirements = Requirements,
            withoutResponds = WithoutRespond
        )
        _meet.emit(meet.value?.copy(id = id))
    }
}