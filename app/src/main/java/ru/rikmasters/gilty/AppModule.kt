package ru.rikmasters.gilty

import org.koin.core.module.Module
import ru.rikmasters.gilty.addmeet.AddMeet
import ru.rikmasters.gilty.auth.Auth
import ru.rikmasters.gilty.chat.Chat
import ru.rikmasters.gilty.chats.ChatData
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.data.ktor.Ktor
import ru.rikmasters.gilty.data.realm.Realm
import ru.rikmasters.gilty.data.reports.ReportsData
import ru.rikmasters.gilty.example.ExampleModule
import ru.rikmasters.gilty.login.Login
import ru.rikmasters.gilty.mainscreen.Main
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.notification.NotificationsData
import ru.rikmasters.gilty.notifications.Notifications
import ru.rikmasters.gilty.profile.Profile
import ru.rikmasters.gilty.profile.ProfileData

object AppModule: FeatureDefinition() {
    
    override fun include(): Set<ModuleDefinition> =
        setOf(
            // Feature
            Notifications, Main,
            AddMeet, Profile, Login, Chat,
            // Data
            Ktor, Auth, ChatData, Realm,
            MeetingsData, NotificationsData,
            ProfileData, ReportsData,
            // Example
            ExampleModule,
        )
    
    override fun DeepNavGraphBuilder.navigation() {}
    
    override fun Module.koin() {}
}