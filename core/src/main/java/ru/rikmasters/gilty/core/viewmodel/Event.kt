package ru.rikmasters.gilty.core.viewmodel

data class Event(
    val key: String,
    val data: Any?
) {
    constructor(pair: Pair<String, Any?>)
            : this(pair.first, pair.second)
}