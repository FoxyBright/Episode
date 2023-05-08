package ru.rikmasters.gilty.bottomsheet.deeplink

import android.content.Intent
import org.koin.core.scope.Scope
import ru.rikmasters.gilty.bottomsheet.deeplink.PushMessageWrapper.LinkType.*
import ru.rikmasters.gilty.bottomsheet.deeplink.PushMessageWrapper.getChatId
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.ui.BottomSheetState
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.navigation.NavState

object DeepLinker {
    
    private fun String.cut(c: String) =
        this.substringAfter(c)
    
    private fun getLink(intent: Intent?) =
        intent?.data.toString().let {
            when {
                it.contains(MEET.value) ->
                    MEET to it.cut(MEET.value)
                
                it.contains(MESSAGE.value) ->
                    MESSAGE to getChatId(it, MESSAGE)
                
                else -> OTHER to it
            }
        }
    
    suspend fun deepLink(
        scope: Scope, asm: AppStateModel,
        intent: Intent?, nav: NavState,
    ) {
        
        log.d("INTENT ->>>\nData: ${intent?.data}")
        
        val link = getLink(intent)
        val bs = asm.bottomSheet
        
        when(link.first) {
            MEET -> bottomSheet(bs, link.second, scope)
            MESSAGE -> nav.navigateAbsolute("chats/chat?id=${link.second}")
            MEETING_CREATED -> {}
            OTHER -> {}
        }
    }
    
    private suspend fun bottomSheet(
        bs: BottomSheetState, link: String, scope: Scope,
    ) = bs.expand { BottomSheet(scope, BsType.MEET, link) }
}