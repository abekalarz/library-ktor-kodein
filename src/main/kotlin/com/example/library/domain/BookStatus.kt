package com.example.library.domain

enum class BookStatus {
    AVAILABLE,
    CHECKED_OUT;

    companion object {
        fun fromBoolean(available: Boolean): BookStatus =
            if (available) AVAILABLE else CHECKED_OUT
    }
}
