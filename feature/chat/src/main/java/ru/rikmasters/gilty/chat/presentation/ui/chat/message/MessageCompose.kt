package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.End
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import ru.rikmasters.gilty.shared.model.chat.MessageList
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.chat.MessageType.MESSAGE
import ru.rikmasters.gilty.shared.model.chat.MessageType.NOTIFICATION
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun MessPreview() {
    GiltyTheme {
        LazyColumn {
            itemsIndexed(MessageList)
            { index, it ->
                Message(
                    MessState(
                        it, (index % 2 == 0),
                        DragRowState(0f),
                        shapes.large
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
    val shape: Shape
)

interface MessCallBack {
    
    fun onImageClick(message: MessageModel) {}
    fun onHiddenClick(message: MessageModel) {}
    fun onSwipe(message: MessageModel) {}
}

@Composable
fun Message(
    state: MessState,
    modifier: Modifier = Modifier,
    callBack: MessCallBack? = null
) {
    val notification = state.message.type == NOTIFICATION
    Row(
        modifier
            .swipeableRow(
                if(!notification)
                    state.dragState
                else DragRowState(0f)
            ) { callBack?.onSwipe(state.message) },
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
                state, Modifier.fillMaxWidth(0.8f),
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
    callback: MessCallBack? = null
) {
    val message = state.message
    val sender = state.sender
    val attach = message.attachments
    Row(
        modifier,
        if(sender) End
        else Start, Bottom
    ) {
        if(!state.sender &&
            message.type != NOTIFICATION
        ) AsyncImage(
            message.sender.avatar.id,
            (null),
            Modifier
                .padding(end = 6.dp)
                .size(24.dp)
                .clip(CircleShape),
            contentScale = Crop,
        )
        
        when(message.type) {
            
            NOTIFICATION -> SystemMessage(
                state.message.notification
            )
            
            MESSAGE -> {
                when {
                    (attach != null) -> Image(
                        attach.type, message,
                        sender, state.shape, callback,
                    )
                    
                    (message.answer != null) -> Text(
                        message, sender, state.shape,
                        message.answer
                    )
                    
                    else -> Text(
                        message, sender,
                        state.shape
                    )
                }
            }
            
            else -> WritingMessage()
        }
    }
}

@Composable
private fun Image(
    type: AttachmentType,
    message: MessageModel,
    sender: Boolean,
    shape: Shape,
    callback: MessCallBack?
) {
    when(type) {
        
        PHOTO -> {
            ImageMessage(
                message, sender, shape
            ) { callback?.onImageClick(message) }
        }
        
        PRIVATE_PHOTO -> {
            HiddenImageMessage(
                message, sender, message.attachments
                    ?.file?.hasAccess ?: false, shape
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
    answer: MessageModel? = null
) {
    TextMessage(
        message, sender,
        Modifier, answer, shape
    )
}