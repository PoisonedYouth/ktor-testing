package com.poisonedyouth.financemanagement.util

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import com.poisonedyouth.financemanagement.user.adapter.persistence.MyPostgreSQLContainer
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class KtorServerTestExtension : BeforeAllCallback, AfterAllCallback {
    private val postgresqlContainer = MyPostgreSQLContainer("postgres:15.3")
        .withUsername("dbuser")
        .withPassword("password")
        .withDatabaseName("test")
        .withExposedPorts(5432)
        .withCreateContainerCmdModifier {
            it.hostConfig?.withPortBindings(PortBinding(Ports.Binding.bindPort(5432), ExposedPort(5432)))
        }

    companion object {
        private lateinit var server: NettyApplicationEngine
    }

    override fun beforeAll(context: ExtensionContext?) {
        postgresqlContainer.start()
        val env = applicationEngineEnvironment {
            config = ApplicationConfig("application-e2e.conf")
            // Public API
            connector {
                host = "0.0.0.0"
                port = 8080
            }
        }
        server = embeddedServer(Netty, env).start(false)
    }

    override fun afterAll(context: ExtensionContext?) {
        server.stop(100, 100)
        postgresqlContainer.stop()
    }
}
