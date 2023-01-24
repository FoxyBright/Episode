package ru.rikmasters.gilty.profile.presentation.ui.settings.categories

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel

@Composable
fun CategoriesScreen(nav: NavState = get()) {
    val context = LocalContext.current
    var alert by remember { mutableStateOf(false) }
    CategoriesContent(
        Modifier,
        CategoriesState(
            listOf(DemoCategoryModel),
            emptyList(), alert
        ),
        object : CategoriesCallback {

            override fun onClose() {
                nav.navigate("settings")
            }

            override fun onCloseAlert(it: Boolean) {
                alert = it
            }

            override fun onCategoryClick(category: CategoryModel) {
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