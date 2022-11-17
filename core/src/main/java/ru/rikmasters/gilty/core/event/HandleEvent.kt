package ru.rikmasters.gilty.core.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.launch

//TODO доделать систему ивентов
@Composable
inline fun <T> handle(event: MutableState<T?>, crossinline block: (T) -> Unit) {

    if(event.value == null)
        return

    LaunchedEffect(event) {
        launch { block(event.value!!) }
        event.value = null
    }
}