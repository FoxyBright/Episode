package ru.rikmasters.gilty

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.example.ExampleModule

object AppModule: FeatureDefinition() {



    override fun Module.koin() {

    }

    override fun include(): Set<ModuleDefinition> =
        setOf(
            ExampleModule
        )

    override fun DeepNavGraphBuilder.navigation() {
        nested("inner", "dst") {
            screen("dst") {
                Column(Modifier.fillMaxSize()) {
                    Text("Inner/Dst")
                    val nav = get<NavState>()
                    Button({ nav.navigateAbsolute("myentrypoint") }) {
                        Text("To myentrypoint", color = MaterialTheme.colorScheme.background)
                    }
                }
            }
        }
    }
}