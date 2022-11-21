package ru.rikmasters.gilty.data.example.model

import ru.rikmasters.gilty.core.data.entity.interfaces.WebEntity
import java.util.UUID

data class ExampleWeb(
    
    override val id: String,
    
    val name: String,
    
    val age: String

): WebEntity<ExampleModel> {
    
    override fun map() = ExampleModel(UUID.fromString(id), name, age.toInt())
}