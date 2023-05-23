package ru.rikmasters.gilty.shared.socket

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

val mapper = jsonMapper {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    propertyNamingStrategy(PropertyNamingStrategies.SnakeCaseStrategy())
    serializationInclusion(JsonInclude.Include.NON_NULL)
    addModule(kotlinModule())
}