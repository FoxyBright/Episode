package ru.rikmasters.gilty.mainscreen

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object MainScreen : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        screen("mainscreen") {

        }

        nested("recommendation", "swipe") {

            screen("swipe") {

            }

            screen("grid") {

            }
        }
    }

    override fun Module.koin() {}
}