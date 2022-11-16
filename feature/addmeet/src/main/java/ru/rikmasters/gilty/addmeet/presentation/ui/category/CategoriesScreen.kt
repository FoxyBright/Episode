package ru.rikmasters.gilty.addmeet.presentation.ui.category

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.ShortCategoryModel

@Composable
fun CategoriesScreen(nav: NavState = get()) {
    val context = LocalContext.current
    CategoriesContent(
        Modifier,
        CategoriesState(DemoShortCategoryModelList, listOf()),
        object : CategoriesCallback {

            override fun onClose() {
                nav.navigateAbsolute("main/meetings")
            }

            override fun onCategoryClick(category: ShortCategoryModel) {
                Toast.makeText(
                    context, "Выбор категории временно не доступен",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNext() {
                nav.navigateAbsolute("addmeet/conditions")
            }
        })
}