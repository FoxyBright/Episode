package ru.rikmasters.gilty.core.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.get
import org.koin.dsl.module
import ru.rikmasters.gilty.core.app.ui.BottomSheetLayout
import ru.rikmasters.gilty.core.app.ui.BottomSheetState
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState
import ru.rikmasters.gilty.core.app.ui.fork.rememberSwipeableState
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.navigation.DeepNavHost
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.util.composable.getOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appEntrypoint(
    theme: AppTheme,
    bottomSheetBackground: @Composable (@Composable () -> Unit) -> Unit,
    snackbar: @Composable (SnackbarData) -> Unit
) {

    val isSystemInDarkMode = isSystemInDarkTheme()
    val systemUiController = rememberSystemUiController()
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetSwipeableState = rememberSwipeableState(BottomSheetSwipeState.COLLAPSED)

    val asm = remember {
        AppStateModel(
            isSystemInDarkMode,
            systemUiController,
            snackbarHostState,
            BottomSheetState(bottomSheetSwipeableState)
        )
    }

    val navController = rememberNavController()
    val entrypointResolver = getOrNull<EntrypointResolver>()
    val startDestination = remember(entrypointResolver) {
        runBlocking { entrypointResolver?.resolve() ?: "entrypoint" }
    }

    val navState = remember(startDestination) {
        NavState(
            navController,
            startDestination
        )
    }

    val env: Environment = get()

    theme.apply(
        asm.darkMode,
        asm.dynamicColor
    ) {
        Box(Modifier.fillMaxSize()) {
            BottomSheetLayout(
                asm.bottomSheetState,
                Modifier,
                bottomSheetBackground,
            ) {
                Scaffold() {
                    DeepNavHost(navState) {
                        env.buildNavigation(this)
                    }
                }
            }
            SnackbarHost(
                snackbarHostState,
                Modifier.align(Alignment.BottomCenter),
                snackbar
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