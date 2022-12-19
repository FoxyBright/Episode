package ru.rikmasters.gilty.mainscreen.presentation.ui.main.swipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.swipeableCard
import ru.rikmasters.gilty.shared.common.MeetingCardCompose
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.DOWN
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.UP
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun MeetingsListContent(
    states: List<Pair<MeetingModel, SwipeableCardState>>,
    modifier: Modifier = Modifier,
    notInteresting: ((state: SwipeableCardState) -> Unit)? = null,
    onSelect: ((MeetingModel, state: SwipeableCardState) -> Unit)? = null,
    onClick: ((MeetingModel) -> Unit)? = null
) {
    Box(modifier.fillMaxSize()) {
        Box(Modifier.padding(16.dp)) {
            Content(states, notInteresting, onSelect, onClick)
        }
    }
}

@Composable
private fun Content(
    states: List<Pair<MeetingModel, SwipeableCardState>>,
    notInteresting: ((state: SwipeableCardState) -> Unit)? = null,
    onSelect: ((MeetingModel, state: SwipeableCardState) -> Unit)? = null,
    onClick: ((MeetingModel) -> Unit)? = null
) {
    states.forEach { (meeting, state) ->
        run {
            if(state.swipedDirection == null) MeetingCardCompose(
                meeting,
                Modifier
                    .fillMaxSize()
                    .clickable { onClick?.let { it(meeting) } }
                    .swipeableCard(
                        {
                            if(it == RIGHT)
                                onSelect?.let { it(meeting, state) }
                        }, state, {}, listOf(DOWN, UP)
                    )
            ) {
                when(it) {
                    RIGHT -> onSelect?.let { it(meeting, state) }
                    LEFT -> notInteresting?.let { it(state) }
                    else -> {}
                }
            }
        }
    }
}