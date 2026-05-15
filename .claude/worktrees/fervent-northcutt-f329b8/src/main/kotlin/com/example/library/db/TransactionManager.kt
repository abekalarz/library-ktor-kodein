package com.example.library.db

import org.jdbi.v3.core.Handle

interface TransactionManager {
    fun <T> inTransaction(block: (Handle) -> T): T
}

class JdbiTransactionManager(private val db: DatabaseFactory) : TransactionManager {
    override fun <T> inTransaction(block: (Handle) -> T): T = db.jdbi.inTransaction<T, Exception> { handle -> block(handle) }
}
