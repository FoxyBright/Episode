package ru.rikmasters.gilty.core.util.composable

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.get
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import java.lang.reflect.InvocationTargetException

@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T: Any> getOrNull(
    qualifier: Qualifier? = null,
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    noinline parameters: ParametersDefinition? = null,
): T? = scope.getOrNull(qualifier, parameters)