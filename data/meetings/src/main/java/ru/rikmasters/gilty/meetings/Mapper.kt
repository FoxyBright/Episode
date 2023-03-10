package ru.rikmasters.gilty.meetings

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

val mapper = jsonMapper {
    propertyNamingStrategy(PropertyNamingStrategies.SnakeCaseStrategy())
    addModule(kotlinModule())
}