package ru.rikmasters.gilty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.log.log
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

        Scaffold {
            NavHost(navController, startDestination = "start") {
                composable("start") {
                    Greeting(name = "SomeName")
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
        Button(
            { env["button"] = env.get<Boolean>("button")?.not() ?: true }
        ) {
            Text("Click me")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        GiltyTheme {
            Greeting("Android")
        }
    }
}