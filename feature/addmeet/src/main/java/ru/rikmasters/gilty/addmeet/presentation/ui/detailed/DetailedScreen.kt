package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.MEETING
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.*
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import java.util.UUID

@Preview
@Composable
fun DetailedScreen(nav: NavState = get()) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    var bsDate by remember { mutableStateOf(TODAY_LABEL) }
    var bsHour by remember { mutableStateOf(TIME_START) }
    var bsMinute by remember { mutableStateOf(TIME_START) }
    var duration by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf(false) }
    val tagList =
        remember { mutableStateListOf("Бэтмэн", "Кингсмен", "Лобби") }
    var time by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var hideMeetPlace by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var meetPlace by
    remember { mutableStateOf<Pair<String, String>?>(null) }
    val context = LocalContext.current
    var placeSearch by remember { mutableStateOf("") }
    val bsDateCallback = object: DateTimeBSCallback {
        override fun dateChange(it: String) {
            bsDate = it
        }
        
        override fun hourChange(it: String) {
            bsHour = it
        }
        
        override fun minuteChange(it: String) {
            bsMinute = it
        }
        
        override fun onSave() {
            date = "$bsDate, $bsHour:$bsMinute"
            val fullTime = "T$bsHour:$bsMinute:00Z"
            val fullDate = if(bsDate == "Сегодня")
                LOCAL_DATE.format("yyyy-MM-dd")
            // TODO год всегда текущий, пересмотреть логику
            else "${LOCAL_DATE.year()}-" + bsDate.format("dd MMMM", "MM-dd")
            MEETING.dateTime = fullDate + fullTime
            scope.launch { asm.bottomSheet.collapse() }
        }
    }
    
    GiltyTheme {
        DetailedContent(
            DetailedState(
                time, date, description,
                tagList, meetPlace, hideMeetPlace, alert, MEETING.isOnline
            ), Modifier, object: DetailedCallback {
                override fun onBack() {
                    nav.navigate("conditions")
                }
                
                override fun onClose() {
                    nav.navigateAbsolute("main/meetings")
                }
                
                override fun onCloseAlert(it: Boolean) {
                    alert = it
                }
                
                override fun onNext() {
                    nav.navigate("requirements")
                }
                
                override fun onDateClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            DateTimeBS(
                                DateTimeBSState(
                                    bsDate, bsHour,
                                    bsMinute, MEETING.isOnline
                                ),
                                Modifier
                                    .padding(16.dp)
                                    .padding(top = 10.dp),
                                bsDateCallback
                            )
                        }
                    }
                }
                
                override fun onTimeClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            DurationBottomSheet(
                                duration, Modifier.padding(16.dp),
                                MEETING.isOnline, {
                                    duration = it
                                    MEETING.duration = it
                                })
                            {
                                scope.launch { asm.bottomSheet.collapse() }
                                time = duration
                            }
                        }
                    }
                }
                
                override fun onTagsClick() {
                    nav.navigate("tags")
                }
                
                override fun onTagDelete(tag: Int) {
                    tagList.removeAt(tag)
                    val tags = arrayListOf<TagModel>()
                    repeat(tagList.size) {
                        tags.add(TagModel(UUID.randomUUID(), tagList[it]))
                    }
                    MEETING.tags = tags
                }
                
                override fun onDescriptionChange(text: String) {
                    description = text
                    MEETING.description = description
                }
                
                override fun onMeetPlaceClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            MapBottomSheet(
                                MapState(placeSearch, MEETING.isOnline), Modifier,
                                object: MapCallback {
                                    override fun onChange(text: String) {
                                        placeSearch = text
                                    }
                                    
                                    override fun onMapClick() {
                                        Toast.makeText(
                                            context, "Для карт пока что отсутствует API",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    
                                    override fun onItemClick(place: Pair<String, String>) {
                                        meetPlace = place
                                        MEETING.address = place.first
                                        MEETING.place = place.second
                                        scope.launch { asm.bottomSheet.collapse() }
                                    }
                                    
                                    override fun onBack() {
                                        scope.launch { asm.bottomSheet.collapse() }
                                    }
                                }
                            )
                        }
                    }
                }
                
                override fun onHideMeetPlaceClick() {
                    hideMeetPlace = !hideMeetPlace
                    MEETING.hideAddress = hideMeetPlace
                }
                
                override fun onDescriptionClear() {
                    description = ""
                    MEETING.description = ""
                }
            }
        )
    }
}