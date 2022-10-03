package ru.rikmasters.gilty

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rikmasters.gilty.presentation.ui.presentation.login.LoginCallback
import ru.rikmasters.gilty.presentation.ui.presentation.login.LoginContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.PermissionsContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.PermissionsContentCallback
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategories
import ru.rikmasters.gilty.presentation.ui.presentation.login.TextContent
import ru.rikmasters.gilty.presentation.ui.presentation.profile.ProfilePreview
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
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
            NavHost(navController, startDestination = "permissions") {
                composable("select") { TextContent() }
                composable("permissions") {
                    PermissionsContent(Modifier, object : PermissionsContentCallback {
                        override fun onBack() {
                            navController.navigate("profile")
                        }
                        override fun onFinish() {
                            Toast.makeText(
                                this@MainActivity,
                                "Дальше еще не нарисовано!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
                composable("profile") { ProfilePreview() }
                composable("login") {
                    LoginContent(
                        Modifier,
                        object : LoginCallback {
                            override fun onNext() {
                                navController.navigate("profile")
                            }

                            override fun googleLogin() {}
                            override fun onOps() {}
                        })
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GiltyTheme {
        Greeting("Android")
    }
}