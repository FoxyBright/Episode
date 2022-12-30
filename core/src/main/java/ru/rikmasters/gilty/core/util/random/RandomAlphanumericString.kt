package ru.rikmasters.gilty.core.util.random

private val alphanumericCharPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')

fun randomAlphanumericString(length: Int) =
    (0 until length).map { alphanumericCharPool.random() }.joinToString("")