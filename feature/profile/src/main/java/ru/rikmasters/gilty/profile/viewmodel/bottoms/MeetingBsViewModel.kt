package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel

class MeetingBsViewModel(
    
    val profileVm: UserProfileViewModel = UserProfileViewModel()

): ViewModel() {
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _memberList = MutableStateFlow(listOf<MemberModel>())
    val memberList = _memberList.asStateFlow()
    
    private val _distance = MutableStateFlow("")
    val distance = _distance.asStateFlow()
    
    suspend fun drawMeet(meet: MeetingModel) {
        _memberList.emit(DemoMemberModelList)
        _distance.emit(distanceCalculator(meet))
    }
    
    suspend fun menuDismiss(state: Boolean) {
        _menu.emit(state)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun outOfMeet() {
        makeToast("Вы покинули meet")
    }
    
    suspend fun cancelOfMeet() {
        makeToast("Вы отменили встречу")
    }
    
    suspend fun sharedMeet() {
        makeToast("ВААААУ, Типа поделился) Съешь пирожок с полки")
    }
}