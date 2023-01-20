package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.ObserversType.OBSERVABLES
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.ObserverBsViewModel.SubscribeType.DELETE
import ru.rikmasters.gilty.profile.viewmodel.ObserverBsViewModel.SubscribeType.SUB
import ru.rikmasters.gilty.profile.viewmodel.ObserverBsViewModel.SubscribeType.UNSUB
import ru.rikmasters.gilty.shared.model.meeting.MemberModel

class ObserverBsViewModel(
    
    private val profileVm: UserProfileViewModel

): ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val _observersSelectTab = MutableStateFlow(listOf(true, false))
    val observersSelectTab = _observersSelectTab.asStateFlow()
    
    private val _observers = MutableStateFlow(listOf<MemberModel>())
    val observers = _observers.asStateFlow()
    
    private val _observables = MutableStateFlow(listOf<MemberModel>())
    val observables = _observables.asStateFlow()
    
    suspend fun changeObserversTab(tab: Int) {
        val list = arrayListOf<Boolean>()
        repeat(observersSelectTab.value.size) { list.add(it == tab) }
        _observersSelectTab.emit(list)
    }
    
    suspend fun getObservers() {
        _observers.emit(profileManager.getObservers(OBSERVERS))
    }
    
    suspend fun getObservables() {
        _observables.emit(profileManager.getObservers(OBSERVABLES))
    }
    
    enum class SubscribeType { SUB, UNSUB, DELETE }
    
    suspend fun onSubScribe(member: MemberModel, type: SubscribeType) {
        when(type) {
            SUB -> {
                profileManager.subscribeToUser(member)
                _observers.emit(observables.value - member)
            }
            
            UNSUB -> {
                profileManager.unsubscribeFromUser(member)
                _observables.emit(observables.value - member)
            }
            
            DELETE -> {
                profileManager.deleteObserver(member)
                getObservers()
            }
        }
    }
}

