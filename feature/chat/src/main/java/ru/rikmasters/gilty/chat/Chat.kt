package ru.rikmasters.gilty.chat

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.chat.presentation.ui.chatList.ChatListScreen
import ru.rikmasters.gilty.chat.presentation.ui.dialog.DialogScreen
import ru.rikmasters.gilty.chat.presentation.ui.photoView.PhotoViewScreen
import ru.rikmasters.gilty.chat.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.chat.viewmodel.DialogViewModel
import ru.rikmasters.gilty.chats.ChatData
import ru.rikmasters.gilty.chats.ChatManager
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.profile.ProfileData
import ru.rikmasters.gilty.profile.ProfileManager

object Chat: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("chats", "main") {
            
            screen<ChatListViewModel>("main") { vm, _ ->
                ChatListScreen(vm)
            }
            
            screen<DialogViewModel>(
                route = "chat?id={id}",
                arguments = listOf(navArgument("id")
                { type = NavType.StringType; defaultValue = "" })
            ) { vm, it -> DialogScreen(vm, it.arguments?.getString("id")!!) }
            screen("photo?type={type}&image={image}&hash={hash}", listOf(
                navArgument("image") {
                    type = NavType.StringType; defaultValue = ""
                }, navArgument("type") {
                    type = NavType.IntType; defaultValue = 0
                }
            )) {
                it.arguments?.getInt("type")?.let { type ->
                    it.arguments?.getString("image")?.let { image ->
                        PhotoViewScreen(image, type)
                    }
                }
            }
        }
    }
    
    override fun Module.koin() {
        singleOf(::ChatManager)
        singleOf(::ProfileManager)
        singleOf(::MeetingManager)
        
        scope<ChatListViewModel> {
            scopedOf(::ChatListViewModel)
        }
        
        scope<DialogViewModel> {
            scopedOf(::DialogViewModel)
        }
    }
    
    override fun include() = setOf(ChatData, ProfileData, MeetingsData)
}