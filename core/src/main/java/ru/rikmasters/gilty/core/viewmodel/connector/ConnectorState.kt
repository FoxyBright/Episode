package ru.rikmasters.gilty.core.viewmodel.connector

import androidx.compose.runtime.*
import ru.rikmasters.gilty.core.viewmodel.ViewModel

@Composable
fun <T: ViewModel> rememberConnectorState(vm: T): ConnectorState<T> =
    remember { ConnectorState(vm) }

@Immutable
class ConnectorState<T: ViewModel>(
    val vm: T,
) { }