package ru.rikmasters.gilty.bottomsheet.viewmodel

import android.content.Context
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.SuggestSession.SuggestListener
import com.yandex.runtime.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.meeting.LocationModel

@OptIn(FlowPreview::class)
class MapBsViewModel: ViewModel(), SuggestListener {
    
    private val resultNumberLimit = 15
    
    private val context = getKoin().get<Context>()
    
    private var searchManager: SearchManager? = null
    private var suggestSession: SuggestSession? = null
    
    private val center = Point(55.75, 37.62) // Moscow coordinates
    
    private val boxSize = 20 // The radius search should cover
    private val boundingBox = BoundingBox(
        Point(
            center.latitude - boxSize,
            center.longitude - boxSize
        ),
        Point(
            center.latitude + boxSize,
            center.longitude + boxSize
        )
    )
    
    private val searchOptions =
        SuggestOptions().setSuggestTypes(
            SuggestType.GEO.value or
                    SuggestType.BIZ.value or
                    SuggestType.TRANSIT.value
        )
    
    private val meetManager by inject<MeetingManager>()
    
    private val _search =
        MutableStateFlow("")
    val search = _search.asStateFlow()
    
    private val _last =
        MutableStateFlow(emptyList<LocationModel>())
    val last = _last.asStateFlow()
    
    private val _searchResult =
        MutableStateFlow(emptyList<LocationModel>())
    val searchResult = _searchResult
        .combine(search.debounce(250)) { _, current ->
            if(current.isEmpty())
                last.value
            else {
                requestSuggest(current)
                last.value
            }
        }.state(_searchResult.value)
    
    init {
        searchManager =
            SearchFactory.getInstance()
                .createSearchManager(SearchManagerType.COMBINED)
        suggestSession = searchManager?.createSuggestSession()
    }
    
    private fun requestSuggest(query: String) {
        this.coroutineScope.launch(Dispatchers.Main) {
            suggestSession?.suggest(
                query,
                boundingBox,
                searchOptions,
                this@MapBsViewModel
            )
        }
    }
    
    suspend fun changeSearch(text: String) {
        _search.emit(text)
        if(text.isEmpty())
            _last.emit(emptyList())
    }
    
    suspend fun getLastPlaces() {
        meetManager.getLastPlaces().on(
            success = { _last.emit(it) },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun selectPlace(
        location: LocationModel,
    ) {
        meetManager.update(
            place = location.place,
            address = location.address,
            lat = location.lat,
            lng = location.lng,
        )
    }
    
    override fun onResponse(suggest: MutableList<SuggestItem>) {
        this.coroutineScope.launch {
            _last.emit(emptyList())
        }
        val suggestResult = mutableListOf<LocationModel>()
        for(i in 0 until resultNumberLimit.coerceAtMost(suggest.size)) {
            with(suggest[i]) {
                val model = LocationModel(
                    hide = false,
                    lat = center?.latitude,
                    lng = center?.longitude,
                    place = title.text,
                    subtitle?.text,
                    country = null
                )
                suggestResult.add(model)
            }
        }
        this.coroutineScope.launch {
            _last.emit(suggestResult)
            _searchResult.emit(suggestResult)
        }
    }
    
    override fun onError(p0: Error) {}
}