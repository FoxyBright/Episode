package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.ObserversType.OBSERVABLES
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel

class ObserverBsViewModel(
    
    private val profileVm: UserProfileViewModel

): ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    //TODO Сделать нормально
    private val _observersSelectTab = MutableStateFlow(listOf(true, false))
    val observersSelectTab = _observersSelectTab.asStateFlow()
    
    suspend fun changeObserversTab(tab: Int) {
        val list = arrayListOf<Boolean>()
        repeat(observersSelectTab.value.size) { list.add(it == tab) }
        _observersSelectTab.emit(list)
    }
    //TODO /////////////
    
    private val _observers = MutableStateFlow(listOf<MemberModel>())
    val observers = _observers.asStateFlow()
    
    private val _observables = MutableStateFlow(listOf<MemberModel>())
    val observables = _observables.asStateFlow()
    
    suspend fun getObservers() {
        _observers.emit(profileManager.getObservers(OBSERVERS))
    }
    
    suspend fun getObservables() {
        _observables.emit(profileManager.getObservers(OBSERVABLES))
    }
    
    suspend fun deleteObservable() {
    }
    
    suspend fun deleteObserver() {
    }
    
    suspend fun selectMember() {
    }
}

