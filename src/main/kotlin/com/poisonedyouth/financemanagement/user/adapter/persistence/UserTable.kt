package com.poisonedyouth.financemanagement.user.adapter.persistence

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

private const val DEFAULT_VARCHAR_COLUMN_SIZE = 50

public object UserTable : UUIDTable("user", "id") {
    public val firstname: Column<String> = varchar("firstname", DEFAULT_VARCHAR_COLUMN_SIZE)
    public val lastname: Column<String> = varchar("lastname", DEFAULT_VARCHAR_COLUMN_SIZE)
    public val email: Column<String> = varchar("email", DEFAULT_VARCHAR_COLUMN_SIZE).uniqueIndex("unique_email")

    public fun initTable() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(this@UserTable)
        }
    }
}
