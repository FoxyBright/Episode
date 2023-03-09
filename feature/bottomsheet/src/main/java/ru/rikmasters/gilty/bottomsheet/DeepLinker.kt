package ru.rikmasters.gilty.bottomsheet

import android.content.Intent
import org.koin.core.scope.Scope
import ru.rikmasters.gilty.bottomsheet.DeepLinker.LinkType.MEET
import ru.rikmasters.gilty.bottomsheet.DeepLinker.LinkType.OTHER
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.ui.BottomSheetState
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED

object DeepLinker {
    
    private fun String.cut(c: String) =
        this.substringAfter(c)
    
    private enum class LinkType(val value: String) {
        OTHER("OTHER"),
        MEET("meet/?")
    }
    
    private fun getLink(intent: Intent?): Pair<String, String> {
        return intent?.data.toString().let {
            when {
                // Ссылка на встречу
                it.contains(MEET.value) -> Pair(
                    MEET.name, it.cut(MEET.value)
                )
                
                // Другие интенты
                else -> Pair(OTHER.name, it)
            }
        }
    }
    
    suspend fun deepLink(
        scope: Scope, asm: AppStateModel, intent: Intent?,
    ) {
        val link = getLink(intent)
        val bs = asm.bottomSheet
        
        // FIXME попытка открытия ссылки при открытом BS не отрабатывает
        if(bs.current.value != COLLAPSED) bs.collapse()
        
        when(link.first) {
            // Ссылка на встречу
            MEET.name -> bottomSheet(bs, link.second, scope)
            
            // Add new deepLink actions
        }
    }
    
    private suspend fun bottomSheet(
        bs: BottomSheetState, link: String, scope: Scope,
    ) = bs.expand { BottomSheet(scope, BsType.MEET, link) }
}