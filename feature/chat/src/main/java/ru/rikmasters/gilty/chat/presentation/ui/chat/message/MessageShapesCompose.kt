package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.chat.MessageType
import ru.rikmasters.gilty.shared.model.chat.MessageType.NOTIFICATION
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Composable
fun MessageShapes(
    type: MessageType,
    sender: Boolean,
    last: MessageModel? = null,
    next: MessageModel? = null
): Shape {
    if(type == NOTIFICATION)
        return shapes.zero
    
    val shape = 14.dp
    val zero = 0.dp
    
    val top = last?.let {
        it.type != NOTIFICATION
    } ?: false
    
    val bottom = next?.let {
        it.type != NOTIFICATION
    } ?: false
    
    return RoundedCornerShape(
        topStart = if(sender || !top)
            shape else zero,
        
        bottomStart = if(sender || !bottom)
            shape else zero,
        
        topEnd = if(!sender || !top)
            shape else zero,
        
        bottomEnd = if(!sender || !bottom)
            shape else zero
    )
}