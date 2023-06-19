package ru.rikmasters.gilty.chat

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.chat.presentation.ui.chat.ChatScreen
import ru.rikmasters.gilty.chat.presentation.ui.chatList.ChatListScreen
import ru.rikmasters.gilty.chat.viewmodel.*
import ru.rikmasters.gilty.chats.ChatData
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.profile.ProfileData
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.translation.bottoms.preview.PreviewBsViewModel

object Chat: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("chats", "main") {
            
            screen<ChatListViewModel>("main") { vm, _ ->
                ChatListScreen(vm)
            }
            
            screen<ChatViewModel>(
                route = "chat?id={id}",
                arguments = listOf(navArgument("id")
                { type = NavType.StringType; defaultValue = "" })
            ) { vm, it ->
                it.arguments?.getString("id")?.let { id ->
                    ChatScreen(vm, id)
                }
            }
        }
    }
    
    override fun Module.koin() {
        singleOf(::ChatManager)
        singleOf(::ProfileManager)
        singleOf(::MeetingManager)
        factoryOf(::ChatViewModel)
        singleOf(::ChatListViewModel)
        singleOf(::GalleryViewModel)
        singleOf(::HiddenBsViewModel)
        singleOf(::PreviewBsViewModel)
    }
    
    override fun include() = setOf(ChatData, ProfileData, MeetingsData)
}