package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.DateTimeBS
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.DateTimeBSCallback
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.DateTimeBSState
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.DurationBottomSheet
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.MapBottomSheet
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.MapCallback
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.MapState
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.extentions.TIME_START
import ru.rikmasters.gilty.shared.common.extentions.TODAY_LABEL
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

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
    val bsDateCallback = object : DateTimeBSCallback {
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
            scope.launch { asm.bottomSheetState.collapse() }
        }
    }

    GiltyTheme {
        DetailedContent(
            DetailedState(
                time, date, description,
                tagList, meetPlace, hideMeetPlace, alert
            ), Modifier, object : DetailedCallback {
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
                        asm.bottomSheetState.expand {
                            DateTimeBS(
                                DateTimeBSState(bsDate, bsHour, bsMinute),
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
                        asm.bottomSheetState.expand {
                            DurationBottomSheet(
                                duration, Modifier.padding(16.dp),
                                { duration = it })
                            {
                                scope.launch { asm.bottomSheetState.collapse() }
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
                }

                override fun onDescriptionChange(text: String) {
                    description = text
                }

                override fun onMeetPlaceClick() {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            MapBottomSheet(
                                MapState(placeSearch), Modifier,
                                object : MapCallback {
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
                                        scope.launch { asm.bottomSheetState.collapse() }
                                    }

                                    override fun onBack() {
                                        scope.launch { asm.bottomSheetState.collapse() }
                                    }
                                }
                            )
                        }
                    }
                }

                override fun onHideMeetPlaceClick() {
                    hideMeetPlace = !hideMeetPlace
                }

                override fun onDescriptionClear() {
                    description = ""
                }
            }
        )
    }
}