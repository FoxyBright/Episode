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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.data.example.DataExampleModule

object ExampleModule : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen("myentrypoint") {
            val vm: ExampleViewModel = get()
            val scope = rememberCoroutineScope()
            val doorsState by vm.doors.collectAsState()
            LaunchedEffect(Unit) {
                vm.doors.collect {
                    log.v("Latest $it")
                }
            }
            Column(Modifier.fillMaxSize()) {
                Text("MyEntrypoint")
                Button({
                    scope.launch { vm.getDoors().let { log.v("$it") } }
                }) {
                    Text("Get doors", color = MaterialTheme.colorScheme.background)
                }
                Button({
                    scope.launch { vm.getDoors(true).let { log.v("$it") } }
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
        singleOf(::ExampleViewModel)
    }
    
    override fun include(): Set<ModuleDefinition> =
        setOf(DataExampleModule)
}