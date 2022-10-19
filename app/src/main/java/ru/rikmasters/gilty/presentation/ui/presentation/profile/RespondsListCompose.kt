package ru.rikmasters.gilty.presentation.ui.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.presentation.model.profile.DemoProfileModel
import ru.rikmasters.gilty.presentation.model.profile.ProfileModel
import ru.rikmasters.gilty.presentation.ui.shared.Divider
import ru.rikmasters.gilty.presentation.ui.shared.RowActionBar
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview
@Composable
private fun RespondsListPreview() {
    GiltyTheme { RespondsList(DemoFullMeetingModel, listOf(DemoProfileModel, DemoProfileModel)) }
}

@Composable
fun RespondsList(
    meet: FullMeetingModel,
    membersList: List<ProfileModel>,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onMemberClick: (() -> Unit)? = null
) {
    LazyColumn(modifier.background(MaterialTheme.colorScheme.background)) {
        item {
            RowActionBar(
                stringResource(R.string.meeting_members),
                "${membersList.size}/${meet.memberCount}"
            ) { onBack?.let { it() } }
        }
        itemsIndexed(membersList) { index, member ->
            Card(
                Modifier.fillMaxWidth(),
                when (index) {
                    0 -> ThemeExtra.shapes.largeTopRoundedShape
                    membersList.size - 1 -> ThemeExtra.shapes.largeBottomRoundedShape
                    else -> RoundedCornerShape(0.dp)
                }, CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onMemberClick?.let { it() } },
                    verticalAlignment = CenterVertically
                ) {
                    AsyncImage(
                        meet.organizer.avatar.id,
                        stringResource(R.string.meeting_avatar),
                        Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "${member.username}, ${member.age}",
                        color = ThemeExtra.colors.mainTextColor,
                        style = ThemeExtra.typography.Body1Sb,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (index < membersList.size - 1) Divider(Modifier.padding(start = 60.dp))
            }
        }

    }
}