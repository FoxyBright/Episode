package ru.rikmasters.gilty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.navigation.DeepNavHost
import ru.rikmasters.gilty.core.navigation.deepNavGraphBuilder
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val env: Environment by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GiltyTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {

        val navController = rememberNavController()

        env.scope.launch {
            navController.currentBackStackEntryFlow.collect {
                log.v("----------------------")
                it.destination.hierarchy.forEach { log.v("H : $it") }
            }
        }

        Scaffold {
            DeepNavHost(navController, startDestination = "entrypoint") {
                screen("entrypoint") {
                    MyScreen(it) {
                        navController.navigate("nested/deep")
                    }
                }
                nested("nested", "deep") {
                    screen("deep") {
                        MyScreen(it) {
                            navController.navigateUp()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MyScreen(entry: NavBackStackEntry, onClick: () -> Unit) {
        Column(Modifier.fillMaxSize()) {
            Text(text = "Screen ${entry.destination.route ?: "null"}")
            Button(onClick) {
                Text("Click me")
            }
        }
    }
}