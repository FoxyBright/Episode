package ru.rikmasters.gilty.auth.saga

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

data class AuthSaga(
    val externalState: String,
    val method: AuthMethod? = null,
    val phone: String? = null
): DomainEntity {
    enum class AuthMethod {
        Otp, External,
    }
}