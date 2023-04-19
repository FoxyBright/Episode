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
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsMap(
                DemoFullMeetingModel,
                "18 км",
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
                DemoFullMeetingModel.copy(isOnline = true),
                pagingPreview(DemoUserModelList),
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
                stringResource(R.string.meeting_distance_from_user),
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
                    (null), Modifier,
                    colorScheme.onTertiary
                )
            }
        }
        Text(
            stringResource(R.string.add_meet_detailed_meet_place_place_holder),
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 14.dp, start = 16.dp,
                    bottom = 12.dp
                ), colorScheme.onTertiary,
            style = typography.headlineSmall
        )
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
                Image(
                    painterResource(
                        if(meet.isPrivate)
                            R.drawable.ic_lock_close
                        else R.drawable.ic_lock_open
                    ), (null), Modifier
                        .padding(start = 8.dp),
                    colorFilter = tint(colorScheme.tertiary)
                )
            }
            if(meet.membersCount > 3) Text(
                stringResource(R.string.meeting_watch_all_members_in_meet),
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
                        BrieflyRow(
                            "${member.username}${
                                if(member.age in 18..99) {
                                    ", ${member.age}"
                                } else ""
                            }",
                            Modifier.padding(12.dp, 8.dp),
                            member.avatar?.thumbnail?.url,
                            member.emoji
                        )
                        Icon(
                            Filled.KeyboardArrowRight,
                            (null), Modifier.padding(end = 16.dp),
                            colorScheme.onTertiary
                        )
                    }; if(membersList.itemSnapshotList.items.size > 1 && index <= membersList.itemSnapshotList.items.size - 2)
                    Divider(Modifier.padding(start = 60.dp))
                }
        }
    }
    if(meet.type == ANONYMOUS) Text(
        stringResource(R.string.meeting_anonymous_members_label), Modifier
            .fillMaxWidth()
            .padding(
                top = 14.dp, start = 16.dp,
                bottom = 12.dp
            ), colorScheme.onTertiary,
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
            text, { if(it.length <= 120) onTextChange(it) },
            Modifier.fillMaxWidth(),
            shape = shapes.medium,
            colors = descriptionColors(online),
            label = if(text.isNotEmpty()) textFieldLabel(
                (true),
                stringResource(R.string.meeting_comment_text_holder)
            ) else null,
            keyboardOptions = Default.copy(
                imeAction = Done, keyboardType = Text,
                capitalization = Sentences
            ),
            placeholder = textFieldLabel(
                (false),
                stringResource(R.string.meeting_comment_text_holder),
                holderFont = typography.bodyMedium
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
    onChange: (Boolean) -> Unit,
) {
    Column(modifier) {
        CheckBoxCard(
            stringResource(R.string.meeting_open_hidden_photos),
            Modifier, state, online = online
        ) { onChange(it) }
        BoxLabel(
            stringResource(R.string.meeting_only_organizer_label),
            Modifier.padding(top = 4.dp, start = 16.dp)
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
    onClick: (() -> Unit)? = null,
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