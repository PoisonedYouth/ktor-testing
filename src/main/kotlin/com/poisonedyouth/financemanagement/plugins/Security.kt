package com.poisonedyouth.financemanagement.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic

public fun Application.configureSecurity() {
    install(Authentication) {
        basic("basic-auth") {
            realm = "basic-auth-realm"
            validate { credentials ->
                if (credentials.name.isNotEmpty() && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
