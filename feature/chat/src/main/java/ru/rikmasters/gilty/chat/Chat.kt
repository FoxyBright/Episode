package ru.rikmasters.gilty.chat

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import ru.rikmasters.gilty.chat.presentation.ui.chat.chatInstance.ChatScreen
import ru.rikmasters.gilty.chat.presentation.ui.chatList.ChatListScreen
import ru.rikmasters.gilty.chat.presentation.ui.photoView.PhotoViewScreen
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Chat: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("chats", "main") {
            
            screen("main") { ChatListScreen() }
            
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
    
    override fun Module.koin() {}
}