package ru.rikmasters.gilty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import org.koin.android.ext.android.inject
import ru.rikmasters.gilty.core.app.appEntrypoint
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.ui.DefaultSnackbar

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val env: Environment by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            appEntrypoint(
                GiltyTheme,
                { DefaultSnackbar(it) }
            )
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