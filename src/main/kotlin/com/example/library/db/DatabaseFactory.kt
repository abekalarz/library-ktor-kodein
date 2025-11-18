package com.example.library.db

import org.jdbi.v3.core.Jdbi

class DatabaseFactory {
    private val jdbcUrl = System.getenv("DB_URL") ?: "jdbc:mariadb://localhost:3306/librarydb"
    private val username = System.getenv("DB_USER") ?: "root"
    private val password = System.getenv("DB_PASS") ?: "root"

    val jdbi: Jdbi = Jdbi.create(jdbcUrl, username, password)

    fun init() {
        val sql = this::class.java.classLoader.getResource("db/schema.sql")!!.readText()
        jdbi.useHandle<Exception> { handle ->
            sql.split(";").filter { it.trim().isNotEmpty() }.forEach { statement ->
                handle.connection.createStatement().use { st -> st.execute(statement) }
            }
        }
        println("✅ Database initialized")
    }
}
