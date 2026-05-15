package com.example.library.db

import com.example.library.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jdbi.v3.core.Jdbi
import java.util.concurrent.TimeUnit

class DatabaseFactory(private val config: DatabaseConfig = DatabaseConfig()) {
    private val ds = HikariConfig().apply {
        jdbcUrl = config.uri
        username = config.username
        password = config.password
        
        poolName = "library-pool"
        minimumIdle = 1
        maximumPoolSize = config.poolSize
        idleTimeout = TimeUnit.MINUTES.toMillis(3)
        maxLifetime = TimeUnit.MINUTES.toMillis(5)
    }

    private val dataSource = HikariDataSource(ds)

    val jdbi: Jdbi = Jdbi.create(dataSource)

    fun init() {
        val sql = this::class.java.classLoader.getResource("db/schema.sql")!!.readText()
        jdbi.useHandle<Exception> { handle ->
            sql.split(";").filter { it.trim().isNotEmpty() }.forEach { statement ->
                handle.connection.createStatement().use { st -> st.execute(statement) }
            }
        }
        println("✅ Database initialized (Pool: ${config.poolSize} max connections)")
    }

    fun close() {
        dataSource.close()
        println("✅ Database connection pool closed")
    }
}
