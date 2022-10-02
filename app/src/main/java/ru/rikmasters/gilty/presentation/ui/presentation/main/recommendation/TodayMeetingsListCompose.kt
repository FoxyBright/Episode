package ru.rikmasters.gilty.presentation.ui.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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


@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
@Composable
private fun MeetingCardPreview() {

    GiltyTheme() {
        //MeetingCardCompose(Modifier.padding(32.dp), model = DemoMeetingModel)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
@Composable
private fun RecommendationComposePreview() {

    GiltyTheme() {
        TodayMeetingsListCompose(meetings = DemoMeetingList)
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
                                    state = state,
                                    blockedDirections = listOf(Direction.Down, Direction.Up),
                                    onSwiped = {},
                                    onSwipeCancel = {}
                                ),
                            model = meeting,
                            state = state)
                    }
                }

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingCardCompose(
    modifier: Modifier = Modifier,
    model: ShortMeetingModel,
    state: SwipeableCardState
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)) {

        Box() {

            AsyncImage(
                model = model.organizer.avatar.id,
                contentDescription = "avatar",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(14.dp)
                    )
                    .fillMaxWidth())
            
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ThemeExtra.colors.cardBackground )) {

                Column(Modifier.padding(16.dp)) {

                    Row(modifier = Modifier.fillMaxWidth()) {

                        Text(text = model.title,
                            modifier = Modifier.weight(1f),
                            style = TextStyle(
                                color = ThemeExtra.colors.mainTextColor,
                                fontSize = 20.sp,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.Bold
                        ))

                        Row(modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()) {

                            Card(
                                Modifier
                                    .padding(end = 4.dp)
                                    .height(31.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorResource(R.color.primary))) {

                                Text(text = model.dateTime.format("HH:mm"),
                                    modifier =
                                    Modifier.padding(
                                        vertical = 5.dp,
                                        horizontal = 12.dp),
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ))
                            }

                            Card(
                                Modifier
                                    .height(31.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            ) {


                            }
                        }

                    }

                    Row(Modifier.fillMaxWidth().padding(top = 13.dp)) {

                        val scope = rememberCoroutineScope()

                        CardButtonCompose(
                            modifier = Modifier
                                .weight(1f),
                            text = stringResource(id = R.string.not_interesting),
                            color = colorResource(id = R.color.primary),
                            icon = R.drawable.ic_cancel) {
                            scope.launch {
                                state.swipe(Direction.Left)
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(13.dp))

                        CardButtonCompose(
                            modifier = Modifier
                                .weight(1f),
                            text = stringResource(id = R.string.meeting_respond),
                            color = colorResource(id = R.color.primary),
                            icon = R.drawable.ic_heart) {
                            scope.launch {
                                state.swipe(Direction.Right)
                            }
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
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ThemeExtra.colors.grayButton)) {

        Row() {
            
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                Modifier.padding(end = 3.dp),
                colorFilter = ColorFilter.tint(color))
            
            Text(
                text = text,
                Modifier.padding(top = 2.dp),
                color = color,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
        }

    }
    
}