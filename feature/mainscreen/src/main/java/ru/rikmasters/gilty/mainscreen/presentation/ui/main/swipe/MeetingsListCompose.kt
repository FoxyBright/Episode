package ru.rikmasters.gilty.mainscreen.presentation.ui.main.swipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.swipeableCard
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen.CardButton
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen.MeetingStates
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.DOWN
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.UP
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MeetingsListContentPreview() {
    GiltyTheme { MeetingsListContent(listOf()) }
}

@Composable
fun MeetingsListContent(
    states: List<Pair<FullMeetingModel, SwipeableCardState>>,
    modifier: Modifier = Modifier,
    notInteresting: ((state: SwipeableCardState) -> Unit)? = null,
    onSelect: ((FullMeetingModel, state: SwipeableCardState) -> Unit)? = null
) {
    Box(modifier.fillMaxSize()) {
        Box(Modifier.padding(16.dp)) {
            states.forEach { (meeting, state) ->
                run {
                    if (state.swipedDirection == null) {
                        MeetingCardCompose(
                            meeting,
                            Modifier
                                .fillMaxSize()
                                .swipeableCard(
                                    { if (it == RIGHT) onSelect?.let { it(meeting, state) } },
                                    state, {}, listOf(DOWN, UP)
                                ),
                        ) {
                            if (it == RIGHT) onSelect?.let { it(meeting, state) }
                            if (it == LEFT) notInteresting?.let { it(state) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MeetingCardCompose(
    meet: FullMeetingModel,
    modifier: Modifier = Modifier,
    onSelect: (DirectionType) -> Unit
) {
    Card(
        modifier,
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(Color.Transparent)
    ) {
        Box {
            AsyncImage(
                meet.organizer.avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxHeight(0.94f),
                contentScale = ContentScale.Crop
            )
            MeetBottom(
                Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 18.dp), meet
            ) { onSelect(it) }
        }
    }
}

@Composable
private fun MeetBottom(
    modifier: Modifier,
    meet: FullMeetingModel,
    onSelect: (DirectionType) -> Unit
) {
    Box(modifier) {
        if (!isSystemInDarkTheme())
            Image(
                painterResource(R.drawable.ic_back_rect),
                null,
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.FillWidth
            )
        Card(
            Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .offset(y = -(18).dp),
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), SpaceBetween) {
                    Text(
                        meet.title, Modifier.weight(1f),
                        MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.labelLarge
                    ); MeetingStates(Modifier.weight(1f), meet)
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp)
                ) {
                    CardButton(
                        Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        stringResource(R.string.not_interesting),
                        meet.isOnline, R.drawable.ic_cancel
                    ) { onSelect(LEFT) }
                    CardButton(
                        Modifier.weight(1f),
                        stringResource(R.string.meeting_respond),
                        meet.isOnline, R.drawable.ic_heart
                    ) { onSelect(RIGHT) }
                }
            }
        }
    }
}