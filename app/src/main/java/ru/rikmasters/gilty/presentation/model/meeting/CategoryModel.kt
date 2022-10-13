package ru.rikmasters.gilty.presentation.model.meeting

import ru.rikmasters.gilty.presentation.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.presentation.model.profile.EmojiModel
import kotlin.random.Random

sealed interface CategoryModel {
    val id: String
    val name: String
    val color: String
    val emoji: EmojiModel
}

data class ShortCategoryModel(
    override val id: String,
    override val name: String,
    override val color: String,
    override val emoji: EmojiModel
) : CategoryModel

data class FullCategoryModel(
    override val id: String,
    override val name: String,
    override val color: String,
    override val emoji: EmojiModel,
    val subcategories: List<String>
) : CategoryModel

val DemoFullCategoryModel = FullCategoryModel(
    id = "id",
    name = "Category",
    color = "#FF4745",
    emoji = DemoEmojiModel,
    subcategories = listOf("Subcategory", "Subcategory", "Subcategory", "Subcategory")
)

val DemoFullCategoryModelList: List<FullCategoryModel> by lazy {
    val categories = arrayListOf<FullCategoryModel>()
    repeat(21) {
        val rand = Random.nextInt(1, 4)
        categories.add(
            FullCategoryModel(
                "$it", "Category $it", when {
                    rand % 3 == 0 -> "#FF4745"
                    rand % 2 == 0 -> "#FFCC00"
                    else -> "#D70015"
                }, DemoEmojiModel,
                listOf("Subcategory", "Subcategory", "Subcategory", "Subcategory")
            )
        )
    }
    return@lazy categories
}

val DemoShortCategoryModel = ShortCategoryModel(
    id = "id",
    name = "Category",
    color = "#FF4745",
    emoji = DemoEmojiModel
)

val DemoShortCategoryModelList: List<ShortCategoryModel> by lazy {
    val categories = arrayListOf<ShortCategoryModel>()
    repeat(21) {
        val rand = Random.nextInt(1, 4)
        categories.add(
            ShortCategoryModel(
                "$it", "Category $it", when {
                    rand % 3 == 0 -> "#FF4745"
                    rand % 2 == 0 -> "#FFCC00"
                    else -> "#D70015"
                }, DemoEmojiModel
            )
        )
    }
    return@lazy categories
}