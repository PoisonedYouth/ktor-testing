package com.poisonedyouth.financemanagement.user.adapter.rest

import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.user.port.NewUserDto
import com.poisonedyouth.financemanagement.user.port.UserDto
import com.poisonedyouth.financemanagement.user.port.UserUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import org.koin.ktor.ext.inject

public suspend fun mapFailureToHttpResponse(call: ApplicationCall, failure: Failure) {
    when (failure) {
        is Failure.ValidationFailure -> call.respond(HttpStatusCode.BadRequest, failure.message)
        is Failure.AlreadyExistFailure -> call.respond(HttpStatusCode.Conflict, failure.message)
        is Failure.NotFoundFailure -> call.respond(HttpStatusCode.NotFound, failure.message)
        else -> call.respond(HttpStatusCode.InternalServerError, failure.message)
    }
}

public fun Application.configureUserRouting() {
    val userUseCase by inject<UserUseCase>()

    routing {
        route("/api/v1/user") {
            post {
                handlePostRequest(userUseCase)
            }
            authenticate("basic-auth") {
                put {
                    handlePutRequest(userUseCase)
                }
                delete {
                    handleDeleteRequest(userUseCase)
                }
                get {
                    handleGetRequest(userUseCase)
                }
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handleGetRequest(
    userUseCase: UserUseCase
) {
    val userId = call.parameters["userId"] ?: ""
    userUseCase.findById(userId)
        .fold(
            { failure -> mapFailureToHttpResponse(call, failure) }
        ) {
            if (it == null) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = "User with id '$userId' does not exist."
                )
                return@fold
            }
            call.respond(status = HttpStatusCode.OK, message = it)
        }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handleDeleteRequest(
    userUseCase: UserUseCase
) {
    val userId = call.parameters["userId"] ?: ""
    userUseCase.delete(userId)
        .fold(
            { failure -> mapFailureToHttpResponse(call, failure) }
        ) {
            call.respond(
                status = HttpStatusCode.OK,
                message = "User with id '$userId' deleted successfully."
            )
        }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handlePutRequest(
    userUseCase: UserUseCase
) {
    val userDto = call.receive<UserDto>()
    userUseCase.update(userDto)
        .fold(
            { failure -> mapFailureToHttpResponse(call, failure) }
        ) {
            call.respond(
                status = HttpStatusCode.OK,
                message = "User with email '${userDto.email}' updated successfully."
            )
        }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handlePostRequest(
    userUseCase: UserUseCase
) {
    val userDto = call.receive<NewUserDto>()
    userUseCase.create(userDto).fold(
        { failure -> mapFailureToHttpResponse(call, failure) }
    ) {
        call.respond(
            status = HttpStatusCode.Created,
            message = mapOf(
                "userId" to it,
                "password" to "password"
            )
        )
    }
}
