package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.presentation.ui.presentation.custom.swipeablecard.Direction
import ru.rikmasters.gilty.presentation.ui.presentation.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.presentation.ui.presentation.custom.swipeablecard.rememberSwipeableCardState
import ru.rikmasters.gilty.presentation.ui.presentation.custom.swipeablecard.swipeableCard
import ru.rikmasters.gilty.presentation.ui.shared.DateTimeCard
import ru.rikmasters.gilty.presentation.ui.shared.categoriesListCard
import ru.rikmasters.gilty.presentation.ui.theme.Gradients
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(showBackground = true)
@Composable
private fun TodayMeetingsListComposePreview() {
    GiltyTheme {
        TodayMeetingsListCompose(DemoMeetingList)
    }
}

@Composable
fun TodayMeetingsListCompose(
    meetings: List<ShortMeetingModel>
) {
    Box(Modifier.fillMaxSize()) {
        val states = meetings.map { it to rememberSwipeableCardState() }
        Box(Modifier.padding(16.dp)) {
            states.forEach { (meeting, state) ->
                run {
                    if (state.swipedDirection == null) {
                        MeetingCardCompose(
                            Modifier
                                .fillMaxSize()
                                .swipeableCard(
                                    state,
                                    {},
                                    {},
                                    listOf(Direction.Down, Direction.Up)
                                ),
                            meeting,
                            state,
                            false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingCardCompose(
    modifier: Modifier = Modifier,
    model: ShortMeetingModel,
    state: SwipeableCardState,
    today: Boolean
) {
    Card(
        modifier,
        MaterialTheme.shapes.large
    ) {
        Box {
            AsyncImage(
                model.organizer.avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxHeight(),
                contentScale = ContentScale.FillBounds
            )
            Card(
                Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight(),
                MaterialTheme.shapes.large,
                CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                val scope = rememberCoroutineScope()
                LazyVerticalGrid(
                    GridCells.Fixed(2),
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Text(
                            model.title,
                            style = ThemeExtra.typography.H3
                        )
                    }
                    item {
                        Box {
                            Row(Modifier.align(Alignment.CenterEnd)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    DateTimeCard(
                                        DemoMeetingModel.dateTime,
                                        Gradients().green(),
                                        today
                                    )
                                    if (!today)
                                        categoriesListCard(
                                            Modifier.padding(top = 8.dp),
                                            DemoMeetingModel,
                                            false
                                        )
                                }
                                if (today)
                                    categoriesListCard(
                                        Modifier.padding(start = 4.dp),
                                        DemoMeetingModel,
                                        false
                                    )
                            }
                        }
                    }
                    item {
                        CardButtonCompose(
                            Modifier.weight(1f),
                            stringResource(R.string.not_interesting),
                            colorResource(R.color.primary),
                            R.drawable.ic_cancel
                        ) {
                            scope.launch {
                                state.swipe(Direction.Left)
                            }
                        }
                    }
                    item {
                        CardButtonCompose(
                            Modifier.weight(1f),
                            stringResource(R.string.meeting_respond),
                            colorResource(R.color.primary),
                            R.drawable.ic_heart
                        ) {
                            scope.launch { state.swipe(Direction.Right) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardButtonCompose(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    icon: Int,
    onClick: () -> Unit,
) {
    Button(
        onClick,
        modifier,
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(ThemeExtra.colors.grayButton)
    ) {
        Row {
            Image(
                painterResource(icon),
                null,
                Modifier.padding(end = 3.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Text(
                text,
                Modifier.padding(top = 2.dp),
                color = color,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}