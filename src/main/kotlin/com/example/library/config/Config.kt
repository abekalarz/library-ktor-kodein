package com.example.library.config

data class DatabaseConfig(
    val uri: String = System.getenv("MYSQL_URL") ?: "jdbc:mariadb://localhost:3306/librarydb",
    val username: String = System.getenv("MYSQL_USERNAME") ?: "root",
    val password: String = System.getenv("MYSQL_ROOT_PASSWORD") ?: "root",
    val poolSize: Int = System.getenv("MYSQL_POOL_SIZE")?.toIntOrNull()?.coerceAtLeast(10) ?: 10
)
