package ru.rikmasters.gilty.shared.common.meetBS

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.*
import ru.rikmasters.gilty.shared.R.string.*
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun MeetingBsButtonsPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            Column(Modifier.padding(16.dp)) {
                MeetingBsButtons((false), (true))
                MeetingBsButtons((true))
                MeetingBsButtons((false), (false), (true))
            }
        }
    }
}

@Preview
@Composable
private fun MeetingBsMapPreview() {
    GiltyTheme {
        val meet = DemoMeetingModel
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsMap(
                DemoMeetingModel,
                distanceCalculator(meet),
                Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsConditionsPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsConditions(
                DemoMeetingModel, Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsParticipantsPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsParticipants(
                getDemoMeetingModel(isOnline = true),
                DemoMemberModelList,
                Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBSCommentPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsComment(
                (""), (false),
                {}, Modifier.padding(16.dp)
            ) {}
        }
    }
}

@Preview
@Composable
private fun MeetingBsHiddenPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsHidden(
                Modifier.padding(16.dp),
                (true), (true)
            ) {}
        }
    }
}


@Composable
fun MeetingBsButtons(
    userInMeet: Boolean = false,
    online: Boolean = false,
    shared: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: ((Int) -> Unit)? = null
) {
    when {
        shared -> {
            Column {
                GradientButton(
                    modifier.padding(top = 20.dp, bottom = 12.dp),
                    stringResource(meeting_shared_button), online = online,
                    icon = ic_shared
                ) { onClick?.let { it(2) } }
                TextButton(
                    Modifier, (null),
                    stringResource(cancel_button)
                ) { onClick?.let { it(3) } }
            }
        }
        
        userInMeet -> TextButton(
            Modifier.padding(
                top = 20.dp,
                bottom = 12.dp
            ), online,
            stringResource(exit_from_meet)
        ) { onClick?.let { it(1) } }
        
        else -> GradientButton(
            modifier.padding(top = 20.dp, bottom = 12.dp),
            stringResource(meeting_respond), online = online
        ) { onClick?.let { it(0) } }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingBsMap(
    meet: MeetingModel,
    distance: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(modifier) {
        Row(Modifier.padding(bottom = 18.dp)) {
            Text(
                stringResource(meeting_distance_from_user),
                Modifier.padding(end = 8.dp),
                colorScheme.tertiary,
                style = typography.labelLarge
            )
            Text(
                distance, Modifier,
                if(meet.isOnline) colorScheme.secondary
                else colorScheme.primary,
                style = typography.labelLarge
            )
        }
        Card(
            { onClick?.let { it() } },
            Modifier.fillMaxWidth(),
            (true), shapes.medium,
            cardColors(colorScheme.primaryContainer)
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp),
                SpaceBetween, CenterVertically
            ) {
                Column(Modifier.fillMaxWidth().weight(1f)) {
                    Text(
                        meet.address, Modifier
                            .padding(top = 8.dp),
                        colorScheme.onTertiary,
                        style = typography.headlineSmall
                    )
                    Text(
                        meet.place, Modifier
                            .padding(top = 2.dp, bottom = 10.dp),
                        colorScheme.tertiary,
                        style = typography.bodyMedium
                    )
                }
                Icon(
                    Filled.KeyboardArrowRight,
                    (null), Modifier,
                    colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
fun MeetingBsConditions(
    meet: MeetingModel,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            stringResource(meeting_terms),
            Modifier, colorScheme.tertiary,
            style = typography.labelLarge
        )
        Card(
            Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            shapes.extraSmall,
            cardColors(
                colorScheme.primaryContainer
            )
        ) {
            Text(
                meet.type.display, Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium
            )
            Divider(Modifier.padding(start = 16.dp))
            Row(
                Modifier.fillMaxWidth(),
                SpaceBetween, CenterVertically
            ) {
                Row(
                    Modifier.padding(16.dp),
                    Start, CenterVertically
                ) {
                    if(meet.condition == MEMBER_PAY)
                        Image(
                            painterResource(ic_money),
                            (null), Modifier
                                .padding(end = 16.dp)
                                .size(24.dp)
                        )
                    Text(
                        meet.condition.display,
                        Modifier, colorScheme.tertiary,
                        style = typography.bodyMedium
                    )
                }
                if(meet.condition == MEMBER_PAY) Text(
                    "${meet.price ?: "0"} ₽",
                    Modifier.padding(end = 16.dp),
                    if(meet.isOnline) colorScheme.secondary
                    else colorScheme.primary,
                    style = typography.headlineLarge
                )
            }
        }
    }
}

@Composable
fun MeetingBsParticipants(
    meet: MeetingModel,
    membersList: List<MemberModel>,
    modifier: Modifier = Modifier,
    onAllViewClick: (() -> Unit)? = null,
    onMemberClick: ((MemberModel) -> Unit)? = null
) {
    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            SpaceBetween, CenterVertically
        ) {
            Row(Modifier, Start, CenterVertically) {
                Text(
                    stringResource(meeting_members),
                    Modifier, colorScheme.tertiary,
                    style = typography.labelLarge
                )
                Text(
                    "${membersList.size}/" +
                            "${meet.memberCount}",
                    Modifier.padding(start = 8.dp),
                    if(meet.isOnline)
                        colorScheme.secondary
                    else colorScheme.primary,
                    style = typography.labelLarge
                )
                Image(
                    painterResource(
                        if(meet.isPrivate)
                            ic_lock_close
                        else ic_lock_open
                    ), (null), Modifier
                        .padding(start = 8.dp),
                    colorFilter = tint(colorScheme.tertiary)
                )
            }
            Text(
                stringResource(meeting_watch_all_members_in_meet),
                Modifier.clickable(
                    MutableInteractionSource(), (null)
                ) { onAllViewClick?.let { it() } },
                if(meet.isOnline) colorScheme.secondary
                else colorScheme.primary,
                style = typography.bodyMedium,
                fontWeight = SemiBold
            )
        }
        Column(
            Modifier
                .padding(top = 12.dp)
                .clip(shapes.large)
                .background(colorScheme.primaryContainer)
        ) {
            membersList.forEachIndexed { index, member ->
                if(index < 3) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onMemberClick?.let { it(member) } },
                        SpaceBetween, CenterVertically
                    ) {
                        BrieflyRow(
                            member.avatar,
                            "${member.username}, ${member.age}",
                            (null), Modifier.padding(12.dp, 8.dp)
                        )
                        Icon(
                            Filled.KeyboardArrowRight,
                            (null), Modifier.padding(end = 16.dp),
                            colorScheme.onTertiary
                        )
                    }
                    val subIndex = index.plus(1)
                    if(subIndex < 3 ||
                        (membersList.size in subIndex..3)
                    ) Divider(Modifier.padding(start = 60.dp))
                }
            }
        }
    }
}

@Composable
fun MeetingBsComment(
    text: String,
    online: Boolean,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTextClear: () -> Unit
) {
    Column(modifier) {
        GTextField(
            text, { if(it.length <= 120) onTextChange(it) },
            Modifier.fillMaxWidth(),
            shape = shapes.medium,
            colors = DescriptionColors(online),
            label = if(text.isNotEmpty()) TextFieldLabel(
                (true), stringResource(meeting_comment_text_holder)
            ) else null,
            placeholder = TextFieldLabel(
                (false), stringResource(meeting_comment_text_holder)
            ), textStyle = typography.bodyMedium,
            clear = onTextClear
        )
        BoxLabel(
            "${text.length}/120",
            Modifier.padding(top = 4.dp),
            TextAlign.End
        )
    }
}

@Composable
fun MeetingBsHidden(
    modifier: Modifier = Modifier,
    state: Boolean,
    online: Boolean,
    onChange: (Boolean) -> Unit
) {
    Column(modifier) {
        CheckBoxCard(
            stringResource(meeting_open_hidden_photos),
            Modifier, state, online = online
        ) { onChange(it) }
        BoxLabel(
            stringResource(meeting_only_organizer_label),
            Modifier.padding(top = 4.dp, start = 16.dp)
        )
    }
}

@Composable
fun BoxLabel(
    text: String,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text, modifier.fillMaxWidth(),
        colorScheme.onTertiary,
        style = typography.headlineSmall,
        textAlign = align
    )
}

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    online: Boolean? = null,
    text: String,
    onClick: (() -> Unit)? = null
) {
    Text(
        text, modifier
            .fillMaxWidth()
            .clickable(
                MutableInteractionSource(), (null)
            ) { onClick?.let { it() } },
        color = online?.let {
            if(it) colorScheme.secondary
            else colorScheme.primary
        } ?: colorScheme.tertiary,
        textAlign = TextAlign.Center,
        style = typography.bodyLarge
    )
}