package ru.rikmasters.gilty.shared.common.extentions

import android.content.Context
import android.content.Context.MODE_PRIVATE

object ChatNotificationBlocker {
    
    private fun Context.editShared(chatId: String) {
        this.getSharedPreferences("sharedPref", MODE_PRIVATE)
            .edit()
            .putString("opened_chat", chatId)
            .apply()
    }
    
    fun blockNotify(context: Context, chatId: String) {
        context.editShared(chatId)
    }
    
    fun clearSelectChat(context: Context) {
        context.editShared("")
    }
}