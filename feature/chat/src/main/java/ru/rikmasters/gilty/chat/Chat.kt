package ru.rikmasters.gilty.chat

import org.koin.core.module.Module
import ru.rikmasters.gilty.chat.presentation.ui.chat.ChatScreen
import ru.rikmasters.gilty.chat.presentation.ui.chatlist.ChatListScreen
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Chat : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        nested("chats", "main"){
            screen("main") { ChatListScreen() }
            screen("chat") { ChatScreen() }
        }
    }

    override fun Module.koin() {}
}