package ru.rikmasters.gilty.core.util.extension


infix fun String?.slash(next: String) =
    if(this.isNullOrBlank())
        next
    else
        (this + if(this.endsWith('/')) next else "/$next")