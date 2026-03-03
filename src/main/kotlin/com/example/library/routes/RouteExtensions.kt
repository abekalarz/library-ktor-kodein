package com.example.library.routes

import com.example.library.api.ErrorResponse
import com.example.library.validation.ValidationError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondValidationError(errors: List<ValidationError>) {
    val errorDetails = errors.associate { it.field to it.message }
    this.respond(
        HttpStatusCode.BadRequest,
        ErrorResponse(
            error = "Validation failed",
            details = errorDetails,
            status = 400
        )
    )
}

