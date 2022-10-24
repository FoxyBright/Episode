package ru.rikmasters.gilty.presentation.ui.presentation.responds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.enumeration.RespondType
import ru.rikmasters.gilty.presentation.model.meeting.MeetingModel
import ru.rikmasters.gilty.presentation.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.presentation.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.presentation.model.notification.RespondModel
import ru.rikmasters.gilty.presentation.model.profile.HiddenPhotoModel
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.BrieflyRowCompose
import ru.rikmasters.gilty.presentation.ui.shared.Divider
import ru.rikmasters.gilty.presentation.ui.shared.SmallButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview
@Composable
private fun RespondsComposePreview() {
    GiltyTheme { RespondsCompose(listOf(DemoSendRespondsModel, DemoReceivedRespondsModel)) }
}

interface RespondsComposeCallback : NavigationInterface {
    fun onCancelClick(meet: MeetingModel)
    fun onRespondClick(meet: MeetingModel)
    fun onAcceptClick(meet: MeetingModel)
    fun onImageClick(image: HiddenPhotoModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RespondsCompose(
    responds: List<RespondModel>,
    modifier: Modifier = Modifier,
    callback: RespondsComposeCallback? = null,
) {
    LazyColumn(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(responds) { respond ->
            Card(
                { callback?.onRespondClick(respond.meet) },
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp), true,
                MaterialTheme.shapes.medium,
                CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                val user = respond.sender
                when (respond.type) {
                    RespondType.SEND -> BrieflyRowCompose(
                        user.avatar, respond.meet.title, null,
                        Modifier.padding(start = 16.dp, top = 12.dp)
                    )

                    RespondType.RECEIVED -> BrieflyRowCompose(
                        user.avatar, "${user.username}, ${user.age}",
                        user.emoji, Modifier.padding(start = 16.dp, top = 12.dp)
                    )
                }
                Column(Modifier.padding(start = 66.dp)) {
                    Divider(Modifier.padding(vertical = 12.dp))
                    respond.comment?.let {
                        Text(
                            respond.comment, Modifier.padding(bottom = 12.dp, end = 20.dp),
                            ThemeExtra.colors.mainTextColor,
                            style = ThemeExtra.typography.Body1Medium
                        )
                    }
                    respond.hiddenPhoto?.let {
                        LazyRow(Modifier.padding(bottom = 12.dp)) {
                            items(respond.hiddenPhoto) {
                                AsyncImage(
                                    it.id,
                                    null,
                                    Modifier
                                        .padding(6.dp)
                                        .size(50.dp)
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .clickable
                                        { callback?.onImageClick(it) },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Row {
                        if (respond.type == RespondType.RECEIVED)
                            SmallButton(
                                stringResource(R.string.notification_respond_accept_button),
                                MaterialTheme.colorScheme.primary,
                                Modifier.padding(bottom = 12.dp, end = 4.dp)
                            ) { callback?.onCancelClick(respond.meet) }
                        SmallButton(
                            stringResource(R.string.notification_respond_cancel_button),
                            ThemeExtra.colors.grayIcon,
                            Modifier.padding(bottom = 12.dp)
                        ) { callback?.onCancelClick(respond.meet) }
                    }
                }
            }
        }
    }
}

