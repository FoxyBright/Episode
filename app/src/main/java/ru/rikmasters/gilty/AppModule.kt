package ru.rikmasters.gilty

import org.koin.core.module.Module
import ru.rikmasters.gilty.addmeet.AddMeet
import ru.rikmasters.gilty.chat.Chat
import ru.rikmasters.gilty.complaints.Complains
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.data.ktor.Ktor
import ru.rikmasters.gilty.data.realm.Realm
import ru.rikmasters.gilty.example.ExampleModule
import ru.rikmasters.gilty.login.Login
import ru.rikmasters.gilty.mainscreen.Main
import ru.rikmasters.gilty.notifications.Notifications
import ru.rikmasters.gilty.profile.Profile

object AppModule : FeatureDefinition() {
    override fun include(): Set<ModuleDefinition> =
        setOf(
            ExampleModule, Login, Main,
            Profile, AddMeet, Notifications,
            Chat, Complains, Realm, Ktor
        )
    
    override fun DeepNavGraphBuilder.navigation() {}

    override fun Module.koin() {}
}