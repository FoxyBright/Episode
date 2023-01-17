package ru.rikmasters.gilty.login

import android.content.Context
import ru.rikmasters.gilty.shared.R

class LoginErrorMessage(
    
    private val error: String,
    
    private val context: Context

) {
    
    val message
        get() = context.getString(
            when(error) {
                "invalid number" -> R.string.login_error_invalid_phone
                "parameters error" -> R.string.login_error_parameters_error
                "Timeout for sending a confirmation code." -> R.string.login_error_timeout
                else -> R.string.login_error_nothing_error
            }
        )
}