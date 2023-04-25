package ru.rikmasters.gilty.mainscreen.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.swipeableCard
import ru.rikmasters.gilty.shared.common.MeetCard
import ru.rikmasters.gilty.shared.common.MeetCardType.MEET
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun MeetingsListContent(
    states: List<Pair<MeetingModel, SwipeableCardState>>,
    modifier: Modifier = Modifier,
    notInteresting: ((MeetingModel, SwipeableCardState) -> Unit)? = null,
    onSelect: ((MeetingModel, SwipeableCardState) -> Unit)? = null,
    onClick: ((MeetingModel) -> Unit)? = null,
) {
    var xOffset by remember { mutableStateOf(0f) }
    
    fun DirectionType.swipe(
        meeting: MeetingModel,
        state: SwipeableCardState,
    ) = when(this) {
        RIGHT -> onSelect?.let { it(meeting, state) }
        LEFT -> notInteresting?.let { it(meeting, state) }
        else -> Unit
    }
    
    val list = remember(states) {
        val size = 3
        if(states.size <= size) states
        else states - states.dropLast(size).toSet()
    }
    
    list.forEachIndexed { index, (meeting, state) ->
        xOffset = state.offset.value.x
        state.swipedDirection ?: run {
            MeetCard(
                modifier = modifier
                    .fillMaxSize()
                    .clickable(
                        MutableInteractionSource(), (null)
                    ) { onClick?.let { it(meeting) } }
                    .swipeableCard(
                        onSwiped = { it.swipe(meeting, state) },
                        state = state
                    ), type = MEET,
                stack = (index == list.size - 2),
                meet = meeting, offset = xOffset
            ) { it.swipe(meeting, state) }
        }
    }
}