package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.profile.DemoRatingModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel

data class FeedBackModel(
    val respond: RespondModel?,
    val ratings: List<RatingModel>?,
)

val DemoFeedBackModel = FeedBackModel(
    DemoRespondModel,
    listOf(
        DemoRatingModel,
        DemoRatingModel,
        DemoRatingModel,
    )
)