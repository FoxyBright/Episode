package ru.rikmasters.gilty.auth.categories

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

data class Category(
    
    val id: String,
    
    val name: String,
    
    val color: String,
    
    val iconType: String,
    
    val children: List<Category>? = listOf()

): DomainEntity