package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVABLES
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel.SubscribeType.DELETE
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel.SubscribeType.SUB
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel.SubscribeType.UNSUB
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class ObserverBsViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val _observersSelectTab = MutableStateFlow(0)
    val observersSelectTab = _observersSelectTab.asStateFlow()
    
    private val _observers = MutableStateFlow(emptyList<UserModel>())
    val observers = _observers.asStateFlow()
    
    private val _observables = MutableStateFlow(emptyList<UserModel>())
    val observables = _observables.asStateFlow()
    
    private val _unsubscribeMembers = MutableStateFlow(emptyList<UserModel>())
    val unsubscribeMembers = _unsubscribeMembers.asStateFlow()
    suspend fun changeObserversTab(tab: Int) {
        _observersSelectTab.emit(tab)
    }
    
    suspend fun getObservers() {
        _observers.emit(profileManager.getObservers(OBSERVERS))
    }
    
    suspend fun getObservables() {
        _observables.emit(profileManager.getObservers(OBSERVABLES))
    }
    
    enum class SubscribeType { SUB, UNSUB, DELETE }
    
    suspend fun unsubscribeMembers() {
        unsubscribeMembers.value.forEach {
            profileManager.unsubscribeFromUser(it.id!!)
        }
        _unsubscribeMembers.emit(emptyList())
    }
    
    suspend fun onSubScribe(member: UserModel, type: SubscribeType) {
        when(type) {
            SUB, UNSUB -> {
                val list = unsubscribeMembers.value
                _unsubscribeMembers.emit(
                    if(list.contains(member)) list - member
                    else list + member
                )
            }
            
            DELETE -> {
                profileManager.deleteObserver(member)
                getObservers()
            }
        }
    }
}

