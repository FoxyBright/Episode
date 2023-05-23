package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.DELETE
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.SUB
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.UNSUB
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVABLES
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class ObserverBsViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val _observersSelectTab = MutableStateFlow(0)
    val observersSelectTab = _observersSelectTab.asStateFlow()
    
    private val _searchObserved = MutableStateFlow("")
    val searchObserved = _searchObserved.asStateFlow()

    private val _searchObservers = MutableStateFlow("")
    val searchObservers = _searchObservers.asStateFlow()
    
    private val _counters = MutableStateFlow(Pair(0, 0))
    val counters = _counters.asStateFlow()
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val searchDebounced = searchObserved
        .debounce(250)
        .onEach {}
        .state(_searchObserved.value, SharingStarted.Eagerly)
    
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun getObservers(type: ObserversType) = lazy {
        when(type){
            OBSERVERS->{
                _searchObserved.debounce(250)
                    .flatMapLatest { query ->
                        profileManager.getObservers(
                            query = query,
                            type = type
                        )
                    }
            }
            OBSERVABLES->{
                _searchObservers.debounce(250)
                    .flatMapLatest { query ->
                        profileManager.getObservers(
                            query = query,
                            type = type
                        )
                    }
            }
        }

    }
    
    val observers by getObservers(OBSERVERS)
    val observables by getObservers(OBSERVABLES)
    
    private val _unsubscribeMembers =
        MutableStateFlow(emptyList<UserModel>())
    val unsubscribeMembers = _unsubscribeMembers.asStateFlow()
    
    suspend fun getCounters() {
        _counters.value = Pair(
            profileManager.getProfile().countWatchers ?: 0,
            profileManager.getProfile().countWatching ?: 0
        )
    }
    
    suspend fun changeObserversTab(tab: Int) {
        _observersSelectTab.emit(tab)
        _searchObserved.emit("")
    }
    
    suspend fun searchObservedChange(text: String) {
        _searchObserved.emit(text)
    }
    suspend fun searchObserversChange(text: String) {
        _searchObservers.emit(text)
    }
    
    suspend fun unsubscribeMembers() {
        unsubscribeMembers.value.forEach {
            it.id?.let { id ->
                profileManager.unsubscribeFromUser(id)
            }
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
            }
        }
    }
}
