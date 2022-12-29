package ru.rikmasters.gilty.mainscreen.presentation.ui.main.swipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.swipeableCard
import ru.rikmasters.gilty.shared.common.MeetingCard
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun MeetingsListContent(
    states: List<Pair<MeetingModel,
            SwipeableCardState>>,
    modifier: Modifier = Modifier,
    notInteresting: ((
        MeetingModel,
        SwipeableCardState
    ) -> Unit)? = null,
    onSelect: ((
        MeetingModel,
        SwipeableCardState
    ) -> Unit)? = null,
    onClick: ((
        MeetingModel
    ) -> Unit)? = null
) {
    Box(modifier.fillMaxSize()) {
        Box(Modifier) {
            Content(
                states, notInteresting,
                onSelect, onClick
            )
        }
    }
}

@Composable
private fun Content(
    states: List<Pair<MeetingModel,
            SwipeableCardState>>,
    notInteresting: ((
        MeetingModel,
        SwipeableCardState
    ) -> Unit)? = null,
    onSelect: ((
        MeetingModel,
        SwipeableCardState
    ) -> Unit)? = null,
    onClick: ((
        MeetingModel
    ) -> Unit)? = null
) {
    var xOffset by remember { mutableStateOf(0f) }
    states.forEach { (
        meeting,
        state) ->
        run {
            xOffset = state.offset.value.x
            state.swipedDirection ?: run {
                fun swipe(
                    type: DirectionType
                ): Unit? {
                    return when(type) {
                        RIGHT -> onSelect?.let {
                            it(meeting, state)
                        }
                        
                        LEFT -> notInteresting?.let {
                            it(meeting, state)
                        }
                        
                        else -> {}
                    }
                }
                MeetingCard(
                    meeting, Modifier
                        .fillMaxSize()
                        .clickable {
                            onClick?.let {
                                it(meeting)
                            }
                        }
                        .swipeableCard({ swipe(it) }, state),
                    xOffset
                ) { swipe(it) }
            }
        }
    }
}