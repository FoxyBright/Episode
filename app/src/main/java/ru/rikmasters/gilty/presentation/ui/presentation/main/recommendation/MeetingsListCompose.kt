package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import ru.rikmasters.gilty.presentation.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.presentation.ui.presentation.custom.swipeablecard.Direction
import ru.rikmasters.gilty.presentation.ui.presentation.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.presentation.ui.presentation.custom.swipeablecard.rememberSwipeableCardState
import ru.rikmasters.gilty.presentation.ui.presentation.custom.swipeablecard.swipeableCard
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra
import ru.rikmasters.gilty.utility.extentions.format

@Preview(showBackground = true)
@Composable
private fun RecommendationComposePreview() {
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
                                    listOf(Direction.Down, Direction.Up)
                                ) {},
                            meeting,
                            state
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
    state: SwipeableCardState
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
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Card(
                Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight(),
                MaterialTheme.shapes.large,
                CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            model.title,
                            Modifier.weight(1f),
                            style = ThemeExtra.typography.H3
                        )
                        Row(
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Card(
                                Modifier
                                    .padding(end = 4.dp)
                                    .height(31.dp),
                                MaterialTheme.shapes.large,
                                CardDefaults.cardColors(colorResource(R.color.primary))
                            ) {
                                Text(
                                    model.dateTime.format("HH:mm"),
                                    Modifier.padding(vertical = 5.dp, horizontal = 12.dp),
                                    style = ThemeExtra.typography.SubHeadBold,
                                    color = ThemeExtra.colors.mainTextColor
                                )
                            }
                            Card(
                                Modifier
                                    .height(31.dp)
                                    .fillMaxWidth(),
                                MaterialTheme.shapes.medium,
                                CardDefaults.cardColors(MaterialTheme.colorScheme.background)
                            ) {}
                        }
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 13.dp)
                    ) {
                        val scope = rememberCoroutineScope()
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
                        Spacer(Modifier.width(13.dp))
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