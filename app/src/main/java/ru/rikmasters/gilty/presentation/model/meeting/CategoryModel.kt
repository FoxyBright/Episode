package ru.rikmasters.gilty.presentation.model.meeting

import ru.rikmasters.gilty.presentation.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.presentation.model.profile.EmojiModel
import kotlin.random.Random

data class CategoryModel(

    val id: String,

    val name: String,

    val color: String,

    val emoji: EmojiModel
)

val DemoCategoryModel = CategoryModel(
    id = "id",
    name = "Название категории",
    color = "#FF4745",
    emoji = DemoEmojiModel
)

val DemoCategoryModelList: List<CategoryModel>
    get() {
        val categories = arrayListOf<CategoryModel>()
        repeat(21) {
            val rand = Random.nextInt(1, 4)
            categories.add(
                CategoryModel(
                    "$it", "Cinema $it", when {
                        rand % 3 == 0 -> "#FF4745"
                        rand % 2 == 0 -> "#FFCC00"
                        else -> "#D70015"
                    }, DemoEmojiModel
                )
            )
        }
        return categories
    }

