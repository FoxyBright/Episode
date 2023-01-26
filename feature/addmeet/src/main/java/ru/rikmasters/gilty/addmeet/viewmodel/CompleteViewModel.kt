package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.addmeet.viewmodel.CompleteViewModel.RequirementType.Companion.get
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.auth.meetings.Location
import ru.rikmasters.gilty.auth.meetings.Requirement
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
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
            randomUUID().toString(), Tags.first(),
            Condition!!, SelectCategory!!, Duration, MeetingType!!, Date,
            profileManager.getProfile().mapToOrganizerModel(), Online,
            Tags.map { TagModel(randomUUID().toString(), it) },
            Description, Private, try {
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
        meetManager.addMeet(
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
            chatForbidden = if(Online) RestrictChat else null,
            tags = Tags,
            description = Description.ifBlank { null },
            dateTime = Date,
            duration = getMinutes(Duration),
            location = if(!Online)
                Location(HideAddress, (0), (0), Place, Address)
            else null,
            isPrivate = Private,
            memberCount = if(Private) 0 else try {
                MemberCount.toInt()
            } catch(e: Exception) {
                0
            },
            requirementsType = get(RequirementsType).name,
            requirements = getRequirements(),
            withoutResponds = WithoutRespond
        )
    }
    
    private fun getRequirements(): List<Requirement>? {
        return when {
            Private -> null
            RequirementsType == 0 -> {
                val req = Requirements.first()
                listOf(
                    Requirement(
                        req.gender, req.ageMin,
                        req.ageMax, (req.orientation!!.id)
                    )
                )
            }
            
            else -> Requirements.map {
                Requirement(
                    it.gender, it.ageMin,
                    it.ageMax, (it.orientation!!.id)
                )
            }
        }
    }
    
    private fun getMinutes(duration: String)
            : Int {
        if(duration == "Сутки") return 1440
        val hourCheck = duration.contains("час")
        val minuteCheck = duration.contains("минут")
        
        fun String.getHours() = this.substringBefore("час")
            .replace(" ", "").toInt()
        
        fun String.getMinutes() = this.reversed().substring(6, 8)
            .reversed().replace(" ", "").toInt()
        
        return when {
            hourCheck && minuteCheck ->
                duration.getHours() * 60 + duration.getMinutes()
            
            hourCheck -> duration.getHours() * 60
            minuteCheck -> duration.getMinutes()
            else -> 0
        }
    }
    
    suspend fun onShared() {
        makeToast("Тут должна быть кнопка поделиться")
        //        val extraText = "https://www.google.com"
        //        val intent = Intent(ACTION_SEND)
        //        intent.type = "text/plain"
        //        intent.putExtra(EXTRA_TEXT, extraText)
        //
        //        ContextCompat.startActivity(
        //            context, createChooser(
        //                intent, ("Поделиться встречей")
        //            ), (null)
        //        )
    }
}