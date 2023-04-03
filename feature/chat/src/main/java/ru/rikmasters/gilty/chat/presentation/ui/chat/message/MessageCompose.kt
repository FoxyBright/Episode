package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.End
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R.drawable.ic_reply
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.swipeableRow
import ru.rikmasters.gilty.shared.model.chat.AttachmentType
import ru.rikmasters.gilty.shared.model.chat.AttachmentType.PHOTO
import ru.rikmasters.gilty.shared.model.chat.AttachmentType.PRIVATE_PHOTO
import ru.rikmasters.gilty.shared.model.chat.AttachmentType.VIDEO
import ru.rikmasters.gilty.shared.model.chat.DemoMessageModelList
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.MESSAGE
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.NOTIFICATION
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import kotlin.random.Random

@Preview
@Composable
fun MessPreview() {
    GiltyTheme {
        LazyColumn {
            items(DemoMessageModelList) {
                Message(
                    MessState(
                        it, (Random.nextBoolean()),
                        DragRowState(0f),
                        shapes.large, (true), (false)
                    ),
                    Modifier
                        .background(colorScheme.background)
                        .padding(
                            16.dp, if(it.type
                                != NOTIFICATION
                            ) 2.dp else 10.dp
                        ),
                )
            }
        }
    }
}

data class MessState(
    val message: MessageModel,
    val sender: Boolean,
    val dragState: DragRowState,
    val shape: Shape,
    val avatar: Boolean,
    val isOnline: Boolean,
)

interface MessCallBack {
    
    fun onImageClick(message: MessageModel) {}
    fun onHiddenClick(message: MessageModel) {}
    fun onAnswerClick(message: MessageModel) {}
    fun onSwipe(message: MessageModel) {}
}

@Composable
fun Message(
    state: MessState,
    modifier: Modifier = Modifier,
    callBack: MessCallBack? = null,
) {
    val message = state.message
    val notification = message.type == NOTIFICATION
    Row(
        modifier
            .swipeableRow(
                if(message.type == MESSAGE)
                    state.dragState
                else DragRowState(0f),
                if(message.type == MESSAGE) {
                    LocalContext.current
                } else null
            ) {
                callBack?.onSwipe(message)
            },
        SpaceBetween, CenterVertically
    ) {
        Box(
            Modifier
                .weight(1f)
                .offset(
                    if(state.sender && !notification)
                        40.dp else 0.dp
                ), if(notification) Center
            else if(state.sender) CenterEnd
            else CenterStart
        ) {
            Content(
                state, Modifier
                    .fillMaxWidth(0.8f),
                callBack
            )
        }
        if(!notification) Image(
            painterResource(ic_reply),
            (null), Modifier
                .padding(start = 12.dp)
                .size(28.dp)
                .offset(50.dp)
        )
    }
}

@Composable
private fun Content(
    state: MessState,
    modifier: Modifier = Modifier,
    callback: MessCallBack? = null,
) {
    val message = state.message
    val sender = state.sender
    val attach = message.message?.attachments
    Row(
        modifier,
        if(sender) End
        else Start, Bottom
    ) {
        if(!state.sender &&
            message.type != NOTIFICATION
            && state.avatar
        ) AsyncImage(
            message.message?.author?.avatar
                ?.thumbnail?.url,
            (null), Modifier
                .padding(end = 6.dp)
                .size(24.dp)
                .clip(CircleShape),
            contentScale = Crop,
        ) else if(
            !state.avatar &&
            message.type != NOTIFICATION
            && !state.sender
        ) {
            Box(
                Modifier
                    .size(30.dp)
                    .padding(end = 6.dp)
            )
        }
        
        when(message.type) {
            
            NOTIFICATION -> SystemMessage(
                state.message.notification
            )
            
            MESSAGE -> {
                when {
                    (!attach.isNullOrEmpty()) -> Image(
                        attach.first().type, message,
                        sender, state.shape,
                        state.isOnline, callback,
                    )
                    
                    (message.replied != null) -> Text(
                        message, sender, state.shape,
                        state.isOnline, message.replied,
                    ) { callback?.onAnswerClick(message.replied!!) }
                    
                    
                    else -> Text(
                        message, sender,
                        state.shape,
                        state.isOnline
                    )
                }
            }
        }
    }
}

@Composable
private fun Image(
    type: AttachmentType,
    message: MessageModel,
    sender: Boolean,
    shape: Shape,
    isOnline: Boolean,
    callback: MessCallBack?,
) {
    when(type) {
        
        PHOTO -> {
            ImageMessage(
                Modifier, message, sender,
                shape, isOnline
            ) { callback?.onImageClick(message) }
        }
        
        PRIVATE_PHOTO -> {
            val hide = message.message?.attachments
            val access = if(hide.isNullOrEmpty()) false
            else hide.first().file?.hasAccess
            HiddenImageMessage(
                Modifier, message, sender, access, shape
            ) { callback?.onHiddenClick(message) }
        }
        
        VIDEO -> {} // TODO Видео в чате
    }
}

@Composable
private fun Text(
    message: MessageModel,
    sender: Boolean,
    shape: Shape,
    isOnline: Boolean,
    answer: MessageModel? = null,
    onClick: (() -> Unit)? = null,
) {
    TextMessage(
        message, sender,
        Modifier, answer,
        shape, isOnline
    ) { onClick?.let { it() } }
}