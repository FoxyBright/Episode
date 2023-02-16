package ru.rikmasters.gilty.chat

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.chat.presentation.ui.chat.chatInstance.ChatScreen
import ru.rikmasters.gilty.chat.presentation.ui.chatList.ChatListScreen
import ru.rikmasters.gilty.chat.presentation.ui.photoView.PhotoViewScreen
import ru.rikmasters.gilty.chat.presentation.ui.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.chat.presentation.ui.viewmodel.ChatViewModel
import ru.rikmasters.gilty.chats.ChatData
import ru.rikmasters.gilty.chats.ChatManager
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Chat: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("chats", "main") {
            
            screen<ChatListViewModel>("main") { vm, _ ->
                ChatListScreen(vm)
            }
            
            screen(
                "chat?type={type}", listOf(
                    navArgument("type") {
                        type = NavType.StringType; defaultValue = ""
                    })
            ) {
                it.arguments?.getString("type")?.let { type ->
                    ChatScreen(type)
                }
            }
            
            screen("photo?image={image}&type={type}", listOf(  //TODO Убрать лишние аргументы
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
        
        scope<ChatListViewModel> {
            scopedOf(::ChatListViewModel)
        }
        
        scope<ChatViewModel> {
            scopedOf(::ChatViewModel)
        }
    }
    
    override fun include() = setOf(ChatData)
}