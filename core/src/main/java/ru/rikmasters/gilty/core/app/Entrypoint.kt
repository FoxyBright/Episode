package ru.rikmasters.gilty.core.app

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.dsl.module
import ru.rikmasters.gilty.core.app.ui.BottomSheetLayout
import ru.rikmasters.gilty.core.app.ui.BottomSheetState
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState
import ru.rikmasters.gilty.core.env.Environment

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

    val scope = rememberCoroutineScope()

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
                    Column(Modifier.background(Color.Cyan)) {
                        TestButton("Switch dark mode") { asm.darkMode = !asm.darkMode }
                        TestButton("Switch dynamic color") { asm.dynamicColor = !asm.dynamicColor }
                        TestButton("Show snackbar") { scope.launch { asm.snackbarHostState.showSnackbar("Message") } }
                        TestButton("Show bottom sheet") {
                            scope.launch {
                                asm.bottomSheetState.halfExpand {
                                    LazyColumn(
                                        Modifier
                                            .background(Color.Yellow)
                                    ) {
                                        items(150) {
                                            Text("#$it Hello world")
                                        }
                                    }
                                }
                            }
                        }
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

    val env: Environment = get()

    LaunchedEffect(env, asm) {
        env.loadModules(
            module { single { asm } },
            reason = asm::class.simpleName
        )
    }
}
@Composable
private fun TestButton(name: String, onClick: () -> Unit) {
    Button(onClick) {
        Text(name, color = MaterialTheme.colorScheme.background)
    }
}