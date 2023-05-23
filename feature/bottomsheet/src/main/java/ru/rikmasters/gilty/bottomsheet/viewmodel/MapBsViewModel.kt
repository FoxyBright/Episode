package ru.rikmasters.gilty.bottomsheet.viewmodel

import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SuggestItem
import com.yandex.mapkit.search.SuggestOptions
import com.yandex.mapkit.search.SuggestSession
import com.yandex.mapkit.search.SuggestSession.SuggestListener
import com.yandex.mapkit.search.SuggestType
import com.yandex.runtime.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.LocationModel

@OptIn(FlowPreview::class)
class MapBsViewModel : ViewModel(), SuggestListener {

    private val RESULT_NUMBER_LIMIT = 15

    private var searchManager: SearchManager? = null
    private var suggestSession: SuggestSession? = null

    private val CENTER = Point(55.75, 37.62) // Moscow coordinates

    private val BOX_SIZE = 20 // The radius search should cover
    private val BOUNDING_BOX = BoundingBox(
        Point(CENTER.latitude - BOX_SIZE, CENTER.longitude - BOX_SIZE),
        Point(CENTER.latitude + BOX_SIZE, CENTER.longitude + BOX_SIZE)
    )
    private val SEARCH_OPTIONS = SuggestOptions().setSuggestTypes(
        SuggestType.GEO.value or
                SuggestType.BIZ.value or
                SuggestType.TRANSIT.value
    )

    private val meetManager by inject<MeetingManager>()

    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()

    private val _last = MutableStateFlow(emptyList<LocationModel>())
    val last = _last.asStateFlow()

    private val _searchResult = MutableStateFlow(emptyList<LocationModel>())
    val searchResult = _searchResult
        .combine(search.debounce(250)) { _, current ->
            if (current.isEmpty())
                last.value
            else {
                requestSuggest(current)
                last.value
            }
        }.state(_searchResult.value)

    init {
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        suggestSession = searchManager?.createSuggestSession()
    }

    private fun requestSuggest(query: String) {
        this.coroutineScope.launch(Dispatchers.Main) {
            suggestSession?.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, this@MapBsViewModel)
        }
    }

    suspend fun changeSearch(text: String) {
        _search.emit(text)
    }

    suspend fun getLastPlaces() {
        _last.emit(meetManager.getLastPlaces())
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
        for (i in 0 until RESULT_NUMBER_LIMIT.coerceAtMost(suggest.size)) {
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