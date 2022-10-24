package ru.rikmasters.gilty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.Navigation
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
        Scaffold {
            Navigation()
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun DefaultPreview() {
    GiltyTheme {
        Greeting("Android")
    }
}