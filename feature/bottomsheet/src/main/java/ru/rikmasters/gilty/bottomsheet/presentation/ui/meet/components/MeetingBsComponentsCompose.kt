package ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
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
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_MEMBER
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun MeetingBsMapPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MeetingBsMap(
                meet = DemoFullMeetingModel,
                distance = "18 км",
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsConditionsPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MeetingBsConditions(
                meet = DemoMeetingModel,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsParticipantsPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MeetingBsParticipants(
                meet = DemoFullMeetingModel
                    .copy(isOnline = true),
                membersList = pagingPreview(
                    DemoUserModelList
                ),
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBSCommentPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
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
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MeetingBsHidden(
                Modifier.padding(16.dp),
                (true), (true)
            ) {}
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingBsMap(
    meet: FullMeetingModel,
    distance: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Column(modifier) {
        Row(Modifier.padding(bottom = 18.dp)) {
            Text(
                text = stringResource(
                    R.string.meeting_distance_from_user
                ),
                modifier = Modifier
                    .padding(end = 8.dp),
                color = colorScheme.tertiary,
                style = typography.labelLarge
            )
            Text(
                text = distance,
                modifier = Modifier,
                color = if(meet.isOnline)
                    colorScheme.secondary
                else colorScheme.primary,
                style = typography.labelLarge
            )
        }
        Card(
            onClick = {
                onClick?.let { it() }
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = true,
            shape = shapes.medium,
            colors = cardColors(
                colorScheme.primaryContainer
            )
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp),
                SpaceBetween, CenterVertically
            ) {
                if(meet.type == ANONYMOUS
                    || (meet.memberState != IS_MEMBER
                            && meet.memberState != IS_ORGANIZER)
                    || meet.address == null
                    || meet.place == null
                    || meet.hideAddress == true
                ) {
                    Text(
                        stringResource(R.string.meeting_watch_on_mup_button),
                        Modifier
                            .weight(1f)
                            .padding(16.dp),
                        colorScheme.tertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                } else Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        meet.address ?: "", Modifier
                            .padding(top = 8.dp),
                        colorScheme.onTertiary,
                        style = typography.headlineSmall
                    )
                    Text(
                        meet.place ?: "", Modifier
                            .padding(top = 2.dp, bottom = 10.dp),
                        colorScheme.tertiary,
                        style = typography.bodyMedium
                    )
                }
                Icon(
                    Filled.KeyboardArrowRight,
                    (null), Modifier.size(28.dp),
                    colorScheme.onTertiary
                )
            }
        }
        when(meet.memberState) {
            IS_MEMBER, IS_ORGANIZER -> Text(
                stringResource(R.string.add_meet_detailed_meet_place_place_holder),
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 14.dp, start = 16.dp,
                        bottom = 12.dp
                    ), colorScheme.onTertiary,
                style = typography.headlineSmall
            )
            else -> Unit
        }
    }
}

@Composable
fun MeetingBsConditions(
    meet: MeetingModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            stringResource(R.string.meeting_terms),
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
            GDivider(Modifier.padding(start = 16.dp))
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
                            painterResource(R.drawable.ic_money),
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
    meet: FullMeetingModel,
    membersList: LazyPagingItems<UserModel>,
    modifier: Modifier = Modifier,
    onAllViewClick: (() -> Unit)? = null,
    onMemberClick: ((UserModel) -> Unit)? = null,
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
                    stringResource(R.string.meeting_members),
                    Modifier, colorScheme.tertiary,
                    style = typography.labelLarge
                )
                Text(
                    "${meet.membersCount}" +
                            if(meet.membersMax > 0)
                                "/" + meet.membersMax else "",
                    Modifier.padding(start = 8.dp),
                    if(meet.isOnline)
                        colorScheme.secondary
                    else colorScheme.primary,
                    style = typography.labelLarge
                )
                if(meet.memberState != IS_ORGANIZER)
                    Image(
                        painter = painterResource(
                            if(meet.memberState != IS_MEMBER
                                || meet.type == ANONYMOUS
                            ) R.drawable.ic_lock_close
                            else R.drawable.ic_lock_open
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        colorFilter = tint(
                            colorScheme.tertiary
                        )
                    )
            }
            Text(
                text = stringResource(R.string.meeting_watch_all_members_in_meet),
                modifier = Modifier.clickable(
                    MutableInteractionSource(),
                    indication = null
                ) { onAllViewClick?.let { it() } },
                color = if(meet.isOnline)
                    colorScheme.secondary
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
            membersList.itemSnapshotList.items.take(3)
                .forEachIndexed { index, member ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                if(meet.type != ANONYMOUS)
                                    onMemberClick?.let { it(member) }
                            },
                        SpaceBetween, CenterVertically
                    ) {
                        val username = "${member.username}${
                            if(member.age in 18..99) {
                                ", ${member.age}"
                            } else ""
                        }"
                        BrieflyRow(
                            text = username,
                            modifier = Modifier
                                .padding(12.dp, 8.dp),
                            image = member.avatar
                                ?.thumbnail?.url,
                            emoji = member.emoji,
                            isOnline = member.isOnline ?: false,
                        )
                        Icon(
                            imageVector = Filled
                                .KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(28.dp),
                            tint = colorScheme.onTertiary
                        )
                    }
                    if(membersList
                            .itemSnapshotList
                            .items.size.let { size ->
                                size > 1 && (index == 0
                                        || (index == 1
                                        && index < size - 1))
                            }
                    ) GDivider(Modifier.padding(start = 60.dp))
                }
        }
    }
    if(meet.type == ANONYMOUS) Text(
        text = stringResource(
            R.string.meeting_anonymous_members_label
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 14.dp,
                start = 16.dp,
                bottom = 12.dp
            ),
        color = colorScheme.onTertiary,
        style = typography.headlineSmall
    )
}

@Composable
fun MeetingBsComment(
    text: String,
    online: Boolean,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTextClear: () -> Unit,
) {
    Column(modifier) {
        GTextField(
            value = text,
            onValueChange = {
                if(it.length <= 120)
                    onTextChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.medium,
            colors = descriptionColors(online),
            label = if(text.isNotEmpty())
                textFieldLabel(
                    label = true,
                    text = stringResource(
                        R.string.meeting_comment_text_holder
                    )
                ) else null,
            keyboardOptions = Default.copy(
                imeAction = Done,
                keyboardType = Text,
                capitalization = Sentences
            ),
            placeholder = textFieldLabel(
                label = false,
                text = stringResource(R.string.meeting_comment_text_holder),
                holderFont = typography.bodyMedium
            ),
            textStyle = typography.bodyMedium,
            clear = onTextClear
        )
        BoxLabel(
            text = "${text.length}/120",
            modifier = Modifier
                .padding(top = 4.dp),
            align = TextAlign.End
        )
    }
}

@Composable
fun MeetingBsHidden(
    modifier: Modifier = Modifier,
    state: Boolean,
    online: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Column(modifier) {
        CheckBoxCard(
            label = stringResource(
                R.string.meeting_open_hidden_photos
            ),
            state = state,
            online = online
        ) { onChange(it) }
        BoxLabel(
            text = stringResource(
                R.string.meeting_only_organizer_label
            ),
            modifier = Modifier.padding(
                top = 4.dp, start = 16.dp
            )
        )
    }
}

@Composable
fun BoxLabel(
    text: String,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        color = colorScheme.onTertiary,
        style = typography.headlineSmall,
        textAlign = align
    )
}

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                MutableInteractionSource(),
                indication = null
            ) { onClick?.let { it() } },
        color = colorScheme.tertiary,
        textAlign = TextAlign.Center,
        style = typography.bodyLarge
    )
}