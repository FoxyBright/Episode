package ru.rikmasters.gilty.shared.wrapper

import io.ktor.utils.io.errors.IOException

class NotSuccessException(
    val status: String
): IOException()