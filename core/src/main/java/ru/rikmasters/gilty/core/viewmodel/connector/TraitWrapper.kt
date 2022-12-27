package ru.rikmasters.gilty.core.viewmodel.connector

import androidx.compose.runtime.Composable

typealias TraitWrapper<VM> = @Composable (VM, @Composable (VM) -> Unit) -> Unit