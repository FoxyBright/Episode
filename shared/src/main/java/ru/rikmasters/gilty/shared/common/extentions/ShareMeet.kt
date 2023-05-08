package ru.rikmasters.gilty.shared.common.extentions

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

fun shareMeet(meetId: String, context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(
        Intent.EXTRA_TEXT,
        ("http://gilty.rikmasters.ru/meet?id=$meetId")
    )
    ContextCompat.startActivity(
        context, Intent.createChooser(
            intent, ("ShareWith")
        ), (null)
    )
}