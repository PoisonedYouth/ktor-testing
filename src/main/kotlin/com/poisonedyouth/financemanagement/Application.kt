package com.poisonedyouth.financemanagement

import com.poisonedyouth.financemanagement.plugins.configureDatabases
import com.poisonedyouth.financemanagement.plugins.configureDependencyInjection
import com.poisonedyouth.financemanagement.plugins.configureSecurity
import com.poisonedyouth.financemanagement.plugins.configureSerialization
import com.poisonedyouth.financemanagement.user.adapter.rest.configureUserRouting
import io.ktor.server.application.Application

public fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function.
public fun Application.module() {
    configureSecurity()
    configureDependencyInjection()
    configureSerialization()
    configureDatabases()
    configureUserRouting()
}
