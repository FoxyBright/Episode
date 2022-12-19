package ru.rikmasters.gilty.chat

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import ru.rikmasters.gilty.chat.presentation.ui.chat.ChatScreen
import ru.rikmasters.gilty.chat.presentation.ui.chatlist.ChatListScreen
import ru.rikmasters.gilty.chat.presentation.ui.photoView.PhotoViewScreen
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Chat: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("chats", "main") {
            
            screen("main") { ChatListScreen() }
            
            screen("chat") { ChatScreen() }
            
            screen("photo?image={image}", listOf(
                navArgument("image") {
                    type = NavType.StringType; defaultValue = ""
                }
            )) {
                it.arguments?.getString("image")?.let { image ->
                    PhotoViewScreen(image)
                }
            }
        }
    }
    
    override fun Module.koin() {}
}