package ru.rikmasters.gilty.shared.model.meeting

import androidx.compose.runtime.Composable

data class FilterModel(
    val name: String,
    val details: String? = null,
    val content: @Composable () -> Unit
)