package ru.rikmasters.gilty.addmeet.viewmodel

import android.content.Context
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

class TagsViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    private val context = getKoin().get<Context>()
    
    val addMeet by lazy { manager.addMeetFlow }
    
    private val _selected =
        MutableStateFlow(emptyList<TagModel>())
    val selected = _selected.asStateFlow()
    
    private val _popular =
        MutableStateFlow(emptyList<TagModel>())
    val popular = _popular.asStateFlow()
    
    private val _search =
        MutableStateFlow("")
    val search = _search.asStateFlow()

    @OptIn(FlowPreview::class)
    @Suppress("Unused")
    private val _searchHelper = _search
        .debounce(250)
        .onEach {
            manager.searchTags(it).on(
                success = {
                    _tags.emit(it)
                },
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
        }
        .state(_search.value, SharingStarted.Eagerly)

    private val _category =
        MutableStateFlow<CategoryModel?>(null)
    val category = _category.asStateFlow()
    
    private val _online =
        MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    private val _tags =
        MutableStateFlow(emptyList<TagModel>())
    
    @OptIn(FlowPreview::class)
    val tags = _search
        .combine(_search.debounce(250)) { _, current ->
            if(current.isNotBlank())
                manager.searchTags(current).on(
                    success = { it },
                    loading = { emptyList() },
                    error = {
                        context.errorToast(
                            it.serverMessage
                        )
                        emptyList()
                    }
                )
            else emptyList()
        }.state(_tags.value)
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _online.emit(it?.isOnline ?: false)
                _category.emit(it?.category)
                _selected.emit(it?.tags ?: emptyList())
            }
        }
    }
    
    suspend fun searchChange(text: String) {
        _search.emit(text)
    }
    
    suspend fun searchClear() {
        _search.emit("")
    }
    
    suspend fun getPopular() {
        manager.getPopularTags(
            listOf(category.value?.id)
        ).on(
            success = { tags ->
                _popular.emit(
                    Tags + tags.filter {
                        !Tags.contains(it)
                    }
                )
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun selectTag(tag: TagModel) {
        val list = selected.value
        _selected.emit(
            if(list.contains(tag))
                list - tag
            else if(list.size < 3)
                list + tag
            else list
        )
    }
    
    @Suppress("unused")
    suspend fun addToPopular(tag: TagModel) {
        _popular.emit(popular.value + tag)
    }
    
    suspend fun deleteTag(tag: TagModel) {
        _selected.emit(selected.value - tag)
    }
    
    suspend fun onSave(list: List<TagModel>) {
        manager.update(tags = list)
    }
}