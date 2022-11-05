package ru.rikmasters.gilty.profile

import androidx.compose.material3.Text
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Profile : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        screen("profile") {
            Text("PROFILE")
        }
    }

    override fun Module.koin() {}
}