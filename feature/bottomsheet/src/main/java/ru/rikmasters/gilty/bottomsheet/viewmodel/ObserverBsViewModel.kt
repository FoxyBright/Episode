package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.DELETE
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.SUB
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.SubscribeType.UNSUB
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class ObserverBsViewModel : ViewModel() {

    private val profileManager by inject<ProfileManager>()

    private val _observersSelectTab = MutableStateFlow(0)
    val observersSelectTab = _observersSelectTab.asStateFlow()

    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()

    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val searchDebounced = search
        .debounce(250)
        .onEach {
            /*
            if(observersSelectTab.value == 0)
                getObservers(it)
            else getObservables(it)

             */
        }
        .state(_search.value, SharingStarted.Eagerly)

    val observers by lazy {
        _search.flatMapLatest { query -> profileManager.getObservers(
            query = query,
            type = OBSERVERS
        )
        }
    }

    val observables by lazy {
        _search.flatMapLatest { query ->
            profileManager.getObservers(
                query = query,
                type = OBSERVERS
            )
        }
    }

    private val _unsubscribeMembers = MutableStateFlow(emptyList<UserModel>())
    val unsubscribeMembers = _unsubscribeMembers.asStateFlow()
    suspend fun changeObserversTab(tab: Int) {
        _observersSelectTab.emit(tab)
        _search.emit("")
    }

    suspend fun searchChange(text: String) {
        _search.emit(text)
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
        when (type) {
            SUB, UNSUB -> {
                val list = unsubscribeMembers.value
                _unsubscribeMembers.emit(
                    if (list.contains(member)) list - member
                    else list + member
                )
            }

            DELETE -> {
                profileManager.deleteObserver(member)
                /*
                getObservers(search.value)

                 */
            }
        }
    }
}
