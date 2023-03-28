package ru.rikmasters.gilty.meetings

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

val mapper = jsonMapper {
    configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    propertyNamingStrategy(SnakeCaseStrategy())
    serializationInclusion(NON_NULL)
    addModule(kotlinModule())
}