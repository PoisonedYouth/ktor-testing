package com.poisonedyouth.financemanagement.plugins

import com.poisonedyouth.financemanagement.user.adapter.persistence.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

private const val MAX_POOL_SIZE = 20

public fun Application.configureDatabases() {
    val databaseConfig = environment.config.config("ktor.database")

    val config = HikariConfig()
    config.driverClassName = databaseConfig.property("driver").getString()
    config.jdbcUrl = databaseConfig.property("url").getString()
    config.username = databaseConfig.property("username").getString()
    config.password = databaseConfig.property("password").getString()
    config.maximumPoolSize = MAX_POOL_SIZE
    config.isAutoCommit = true
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()
    val datasource = HikariDataSource(config)

    val database = Database.connect(
        datasource
    )

    transaction(database) {
        UserTable.initTable()
    }
}
