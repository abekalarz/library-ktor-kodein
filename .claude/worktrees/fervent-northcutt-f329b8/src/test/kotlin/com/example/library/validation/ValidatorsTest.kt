package com.example.library.validation

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull

class ValidatorsTest {
    @Test
    fun `notBlank - when value is null returns error`() {
        val error = Validators.notBlank("name", null)
        expectThat(error).isNotNull().and {
            get { field }.isEqualTo("name")
            get { message }.isEqualTo("name cannot be empty")
        }
    }

    @Test
    fun `notBlank - when value is blank returns error`() {
        val error = Validators.notBlank("name", "   ")
        expectThat(error).isNotNull()
    }

    @Test
    fun `notBlank - when value is valid returns null`() {
        val error = Validators.notBlank("name", "John")
        expectThat(error).isNull()
    }

    @Test
    fun `minLength - when value is too short returns error`() {
        val error = Validators.minLength("name", "ab", 3)
        expectThat(error).isNotNull().and {
            get { field }.isEqualTo("name")
            get { message }.isEqualTo("name must be at least 3 characters long")
        }
    }

    @Test
    fun `minLength - when value meets minimum length returns null`() {
        val error = Validators.minLength("name", "abc", 3)
        expectThat(error).isNull()
    }

    @Test
    fun `positive - when value is zero returns error`() {
        val error = Validators.positive("userId", 0)
        expectThat(error).isNotNull()
    }

    @Test
    fun `positive - when value is negative returns error`() {
        val error = Validators.positive("userId", -1)
        expectThat(error).isNotNull()
    }

    @Test
    fun `positive - when value is positive returns null`() {
        val error = Validators.positive("userId", 1)
        expectThat(error).isNull()
    }
}
