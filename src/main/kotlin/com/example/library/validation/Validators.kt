package com.example.library.validation

data class ValidationError(
    val field: String,
    val message: String
)

// Validator helper functions
object Validators {
    fun notBlank(field: String, value: String?): ValidationError? {
        return if (value == null || value.isBlank()) {
            ValidationError(field, "$field cannot be empty")
        } else null
    }

    fun minLength(field: String, value: String?, minLength: Int): ValidationError? {
        return if (value != null && value.length < minLength) {
            ValidationError(field, "$field must be at least $minLength characters long")
        } else null
    }

    fun positive(field: String, value: Int?): ValidationError? {
        return if (value == null || value <= 0) {
            ValidationError(field, "$field must be a positive number")
        } else null
    }
}
