package com.example.library.api

data class ErrorResponse(
    val error: String,
    val details: Map<String, String> = emptyMap(),
    val status: Int
)
