package ru.rikmasters.gilty.example

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
import ru.rikmasters.gilty.data.example.DataExampleModule

object ExampleModule : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen("myentrypoint") {
            Column(Modifier.fillMaxSize()) {
                Text("MyEntrypoint")
                val nav = get<NavState>()
                Button({ nav.navigate("inner/dst") }) {
                    Text("To inner/dst", color = MaterialTheme.colorScheme.background)
                }
            }
        }
    }

    override fun Module.koin() {
    
    }
    
    override fun include(): Set<ModuleDefinition> =
        setOf(DataExampleModule)
}