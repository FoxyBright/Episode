package ru.rikmasters.gilty.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.data.example.DataExampleModule
import ru.rikmasters.gilty.data.example.repository.ExampleRepository

object ExampleModule : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen("myentrypoint") {
            val repository: ExampleRepository = get()
            val scope = rememberCoroutineScope()
            val flow = remember { repository.doorsFlow() }
            val doorsState by flow.collectAsState(emptyList())
            LaunchedEffect(Unit) {
                flow.collectLatest {
                    log.v("Latest $it")
                }
            }
            Column(Modifier.fillMaxSize()) {
                Text("MyEntrypoint")
                Button({
                    scope.launch { repository.getDoors().let { log.v("$it") } }
                }) {
                    Text("Get doors", color = MaterialTheme.colorScheme.background)
                }
                Button({
                    scope.launch { repository.getDoors(true).let { log.v("$it") } }
                }) {
                    Text("Get doors force web", color = MaterialTheme.colorScheme.background)
                }
                LazyColumn {
                    items(doorsState) {
                        Text(it.name)
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