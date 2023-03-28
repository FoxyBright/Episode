package ru.rikmasters.gilty.core.util.extension


infix fun String?.slash(next: String) =
    if(this.isNullOrBlank())
        next
    else
        (this +
                if(this.endsWith('/'))
                    next.startsWithSlash(false)
                else next.startsWithSlash())

fun String?.startsWithSlash(must: Boolean = true): String? {
    this ?: return null
    val starts = startsWith('/')
    return when {
        must && !starts -> "/$this"
        !must && starts -> this.substring(1)
        else -> this
    }
}