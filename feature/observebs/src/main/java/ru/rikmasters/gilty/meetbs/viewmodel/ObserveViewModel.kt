package ru.rikmasters.gilty.meetbs.viewmodel

import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType.*

class ObserveViewModel: ViewModel() {
    
    private val _meeting = MutableStateFlow<String?>(null)
    val meeting = _meeting.asStateFlow()
    
    private val _user = MutableStateFlow<String?>(null)
    val user = _user.asStateFlow()
    
    suspend fun setUser(userId: String?) {
        userId?.let { _user.emit(it) }
    }
    
    suspend fun setMeeting(meetId: String?) {
        _meeting.emit(meetId)
    }
    
    suspend fun navigate(
        nav: NavHostController,
        type: ObserveType,
        params: String,
    ) {
        when(type) {
            USER -> _user.emit(params)
            MEET -> _meeting.emit(params)
            REPORTS -> {}
            RESPONDS -> {}
            OBSERVERS -> {}
            PARTICIPANTS -> _meeting.emit(params)
        }
        nav.navigate(type.name)
    }
}