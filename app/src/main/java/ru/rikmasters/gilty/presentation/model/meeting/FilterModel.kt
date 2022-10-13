package ru.rikmasters.gilty.presentation.model.meeting

import androidx.compose.runtime.Composable


data class FilterModel(
    val name: String,
    val content: @Composable () -> Unit
)