package ru.rikmasters.gilty.mainscreen.presentation.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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

    val list = remember(states) {
        if (states.size <= 2) states
        else states - states.dropLast(2).toSet()
    }

    list.forEachIndexed { index, state ->
        EpisodeCard(
            meeting = state.first,
            state = state.second,
            stack = index < states.lastIndex,
            modifier = modifier,
            notInteresting = notInteresting,
            onSelect = onSelect,
            onClick = onClick
        )
    }
}

@Composable
private fun EpisodeCard(
    meeting: MeetingModel,
    state: SwipeableCardState,
    stack: Boolean,
    modifier: Modifier = Modifier,
    notInteresting: ((MeetingModel, SwipeableCardState) -> Unit)? = null,
    onSelect: ((MeetingModel, SwipeableCardState) -> Unit)? = null,
    onClick: ((MeetingModel) -> Unit)? = null,
) {
    state.swipedDirection ?: run {
        MeetCard(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    MutableInteractionSource(),
                    indication = null
                ) { onClick?.let { it(meeting) } }
                .swipeableCard(
                    onSwiped = {
                        it.swipe(
                            meeting = meeting,
                            state = state,
                            onSelect = onSelect,
                            notInteresting = notInteresting
                        )
                    },
                    state = state,
                ),
            type = MEET,
            stack = stack,
            meet = meeting,
            offset = state.offset.value.x
        ) {
            it.swipe(
                meeting = meeting,
                state = state,
                onSelect = onSelect,
                notInteresting = notInteresting
            )
        }
    }
}

private fun DirectionType.swipe(
    meeting: MeetingModel,
    state: SwipeableCardState,
    onSelect: ((MeetingModel, SwipeableCardState) -> Unit)?,
    notInteresting: ((MeetingModel, SwipeableCardState) -> Unit)?,
) = when(this) {
    RIGHT -> onSelect?.let { it(meeting, state) }
    LEFT -> notInteresting?.let { it(meeting, state) }
    else -> Unit
}