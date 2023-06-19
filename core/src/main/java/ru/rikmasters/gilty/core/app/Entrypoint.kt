package ru.rikmasters.gilty.core.app

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.get
import org.koin.dsl.module
import ru.rikmasters.gilty.core.app.ui.BottomSheetLayout
import ru.rikmasters.gilty.core.app.ui.BottomSheetState
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED
import ru.rikmasters.gilty.core.app.ui.fork.rememberSwipeableState
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.navigation.DeepNavHost
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait.Companion.loader
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait.Companion.indicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppEntrypoint(theme: AppTheme) {
    val entrypointResolver =
        get<EntrypointResolver>()
    
    val snackbarHostState =
        remember { SnackbarHostState() }
    
    val systemUiController =
        rememberSystemUiController()
    
    val keyboardController =
        rememberKeyboardController()
    
    val navController =
        rememberNavController()
    
    val bottomSheetState = BottomSheetState(
        rememberSwipeableState(COLLAPSED)
    )
    
    val env = get<Environment>()
    
    val startDestination = remember(entrypointResolver) {
        runBlocking { entrypointResolver.resolve() }
    }
    
    val navState = remember(startDestination) {
        NavState(
            navHostController = navController,
            startDestination = startDestination
        )
    }
    
    val asm = remember {
        AppStateModel(
            systemUi = systemUiController,
            bottomSheet = bottomSheetState,
            keyboard = keyboardController
        )
    }
    
    loader = { isLoading, content ->
        GLoader(
            isLoading = isLoading,
            content = content
        )
    }
    
    indicator = { state, offset, trigger ->
        LoadingIndicator(
            swipeRefreshState = state,
            offset = offset,
            trigger = trigger
        )
    }
    
    LaunchedEffect(env, asm) {
        loadAsModule(env, asm)
    }
    LaunchedEffect(env, navState) {
        loadAsModule(env, navState)
    }
    
    theme.apply(
        darkMode = isSystemInDarkTheme(),
        dynamicColor = false
    ) {
        Layout(
            bottomSheet = asm.bottomSheet,
            navState = navState,
            background = colorScheme.background,
            env = env,
            snack = snackbarHostState
        )
    }
}

@Composable
private fun Layout(
    bottomSheet: BottomSheetState,
    navState: NavState,
    background: Color,
    env: Environment,
    snack: SnackbarHostState,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        BottomSheetLayout(
            state = bottomSheet,
            background = { BSContent(it) }
        ) {
            Box(Modifier.background(background)) {
                DeepNavHost(navState) {
                    env.buildNavigation(this)
                }
            }
        }
        SnackbarHost(
            hostState = snack,
            modifier = Modifier.align(BottomCenter),
            snackbar = { GSnackbar(it) }
        )
    }
}

@Composable
private fun BSContent(
    content: (@Composable () -> Unit)?,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                color = content?.let {
                    colorScheme.background
                } ?: Transparent,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
    ) { content?.let { it() } }
}

private inline fun <reified T> loadAsModule(
    env: Environment, module: T,
) = env.loadModules(
    module { single { module } },
    reason = T::class.simpleName
)