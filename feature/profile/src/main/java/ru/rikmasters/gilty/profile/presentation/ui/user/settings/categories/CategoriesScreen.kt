package ru.rikmasters.gilty.profile.presentation.ui.user.settings.categories

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType

@Composable
fun CategoriesScreen(nav: NavState = get()) {
    val context = LocalContext.current
    var alert by remember { mutableStateOf(false) }
    CategoriesContent(
        Modifier,
        CategoriesState(
            CategoriesType.list(),
            listOf(), alert
        ),
        object : CategoriesCallback {

            override fun onClose() {
                nav.navigate("settings")
            }

            override fun onCloseAlert(it: Boolean) {
                alert = it
            }

            override fun onCategoryClick(category: CategoriesType) {
                Toast.makeText(
                    context, "Выбор категории временно не доступен",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNext() {
                nav.navigate("settings")
            }
        })
}