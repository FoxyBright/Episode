package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.NOTIFICATION
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Composable
fun messageShapes(
    type: MessageType,
    sender: Boolean,
    last: MessageModel? = null,
    next: MessageModel? = null,
    zeroShape: Dp = 0.dp,
    roundedShape: Dp = 14.dp,
) = if(type == NOTIFICATION) shapes.zero
else RoundedCornerShape(
    topStart = when {
        sender -> roundedShape
        last == null -> roundedShape
        last.type == NOTIFICATION -> roundedShape
        else -> zeroShape
    },
    bottomStart = when {
        sender -> roundedShape
        next == null -> zeroShape
        next.type == NOTIFICATION -> zeroShape
        else -> zeroShape
    },
    topEnd = when {
        !sender -> roundedShape
        last == null -> roundedShape
        last.type == NOTIFICATION -> roundedShape
        else -> zeroShape
    },
    bottomEnd = when {
        !sender -> roundedShape
        next == null -> zeroShape
        next.type == NOTIFICATION -> zeroShape
        else -> zeroShape
    }
)