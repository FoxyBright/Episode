package ru.rikmasters.gilty.core.app

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefreshState
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
import ru.rikmasters.gilty.core.util.composable.getOrNull
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppEntrypoint(
    theme: AppTheme,
    bottomSheetBackground: @Composable (@Composable () -> Unit) -> Unit,
    snackbar: @Composable (SnackbarData) -> Unit,
    loader: (@Composable (isLoading: Boolean, content: @Composable () -> Unit) -> Unit)? = null,
    indicator: (@Composable (state: SwipeRefreshState, offset: Dp, trigger: Dp) -> Unit)? = null,
) {
    
    LoadingTrait.loader = loader
    PullToRefreshTrait.indicator = indicator
    
    val isSystemInDarkMode = isSystemInDarkTheme()
    val systemUiController = rememberSystemUiController()
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetSwipeableState =
        rememberSwipeableState(COLLAPSED)
    val keyboardController = rememberKeyboardController()
    
    val asm = remember {
        AppStateModel(
            isSystemInDarkMode = isSystemInDarkMode,
            systemUi = systemUiController,
            snackbar = snackbarHostState,
            bottomSheet = BottomSheetState(bottomSheetSwipeableState),
            keyboard = keyboardController
        )
    }
    
    val navController = rememberNavController()
    val entrypointResolver = getOrNull<EntrypointResolver>()
    val startDestination = remember(entrypointResolver) {
        runBlocking { entrypointResolver?.resolve() ?: "entrypoint" }
    }
    
    val navState = remember(startDestination) {
        NavState(navController, startDestination)
    }
    
    val env: Environment = get()
    
    theme.apply(
        darkMode = asm.darkMode,
        dynamicColor = asm.dynamicColor
    ) {
        
        val backgroundColor = colorScheme.background
        
        LaunchedEffect(backgroundColor) {
            asm.systemUi.setStatusBarColor(backgroundColor)
        }
        
        Box(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            BottomSheetLayout(
                state = asm.bottomSheet,
                modifier = Modifier,
                background = bottomSheetBackground,
            ) {
                Box(Modifier.background(backgroundColor)) {
                    DeepNavHost(navState) {
                        env.buildNavigation(this)
                    }
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(BottomCenter),
                snackbar = snackbar
            )
        }
    }
    
    LaunchedEffect(env, asm) {
        loadAsModule(env, asm)
    }
    LaunchedEffect(env, navState) {
        loadAsModule(env, navState)
    }
}

private inline fun <reified T> loadAsModule(env: Environment, module: T) {
    env.loadModules(
        module { single { module } },
        reason = T::class.simpleName
    )
}