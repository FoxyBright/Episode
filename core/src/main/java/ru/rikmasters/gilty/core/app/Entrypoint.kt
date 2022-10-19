package ru.rikmasters.gilty.core.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.dsl.module
import ru.rikmasters.gilty.core.env.Environment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appEntrypoint(
    theme: AppTheme,
    /*bottomBar: @Composable () -> Unit,*/
    snackbar: @Composable (SnackbarData) -> Unit
) {

    val isSystemInDarkMode = isSystemInDarkTheme()
    val systemUiController = rememberSystemUiController()
    val snackbarHostState = remember { SnackbarHostState() }

    val asm = remember {
        AppStateModel(
            isSystemInDarkMode,
            systemUiController,
            snackbarHostState
        )
    }

    val scope = rememberCoroutineScope()

    theme.apply(
        asm.darkMode,
        asm.dynamicColor
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState, snackbar = snackbar)
            }
        ) {
            Column {
                TestButton("Switch dark mode") { asm.darkMode = !asm.darkMode }
                TestButton("Switch dynamic color") { asm.dynamicColor = !asm.dynamicColor }
                TestButton("Show snackbar") { scope.launch { asm.snackbarHostState.showSnackbar("Message") } }
            }
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