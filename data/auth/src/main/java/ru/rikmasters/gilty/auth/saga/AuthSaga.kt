package ru.rikmasters.gilty.auth.saga

import ru.rikmasters.gilty.auth.login.SendCode
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

data class AuthSaga(
    val externalState: String,
    val externalToken: String? = null,
    val method: AuthMethod? = null,
    val phone: String? = null,
    val sendCode: SendCode? = null
): DomainEntity {
    enum class AuthMethod {
        Otp, External,
    }
}