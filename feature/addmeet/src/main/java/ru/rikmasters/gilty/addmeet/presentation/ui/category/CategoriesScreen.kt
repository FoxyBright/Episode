package ru.rikmasters.gilty.addmeet.presentation.ui.category

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.ShortCategoryModel

@Composable
fun CategoriesScreen(nav: NavState = get()) {
    val context = LocalContext.current
    var alert by remember { mutableStateOf(false) }
    CategoriesContent(
        Modifier,
        CategoriesState(DemoShortCategoryModelList, listOf(), alert),
        object : CategoriesCallback {

            override fun onClose() {
                nav.navigateAbsolute("main/meetings")
            }

            override fun onCloseAlert(it: Boolean) {
                alert = it
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