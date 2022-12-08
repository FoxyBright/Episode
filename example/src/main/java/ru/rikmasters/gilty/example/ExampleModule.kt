package ru.rikmasters.gilty.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.data.example.DataExampleModule
import ru.rikmasters.gilty.data.example.repository.ExampleRepository
import java.util.UUID

object ExampleModule : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen("myentrypoint") {
            val repository: ExampleRepository = get()
            val scope = rememberCoroutineScope()
            Column(Modifier.fillMaxSize()) {
                Text("MyEntrypoint")
                
                Row {
                    Button({
                        scope.launch(Dispatchers.IO) {
                            log.v(repository.get(UUID.randomUUID()).toString())
                        }
                    }) {
                        Text("Test data", color = MaterialTheme.colorScheme.background)
                    }
                }
                
                Row {
                    Button({
                        scope.launch(Dispatchers.IO) {
                            log.v("Asking for Bob...")
                            log.v(repository.getDomainOnly("Bob").toString())
                        }
                    }) {
                        Text("Test domain only", color = MaterialTheme.colorScheme.background)
                    }
                }
            }
        }
    }

    override fun Module.koin() {
    
    }
    
    override fun include(): Set<ModuleDefinition> =
        setOf(DataExampleModule)
}