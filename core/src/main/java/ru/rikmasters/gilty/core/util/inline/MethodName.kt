package ru.rikmasters.gilty.core.util.inline

@Suppress("NOTHING_TO_INLINE")
inline fun methodName(): String =
    object{}.javaClass.enclosingMethod!!.name