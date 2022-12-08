package ru.rikmasters.gilty.core.data.http

data class Request(
    
    val path: String,
    
    val method: HttpMethod,
    
    val pathVars: Map<String, String>,
    
    val queryVars: Map<String, String>,
    
    
)