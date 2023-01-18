package ru.rikmasters.gilty.auth.categories

import io.ktor.client.request.get
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.wrapper.wrapped

class CategoriesWebSource: KtorSource() {
    
    suspend fun getCategoriesList(): List<Category> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/categories"
        ).wrapped()
    }
    
    
}