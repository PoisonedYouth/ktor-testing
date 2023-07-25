package com.poisonedyouth.financemanagement.failure

import arrow.core.Either
import org.slf4j.Logger

public sealed interface Failure {
    public val message: String

    public data class ValidationFailure(override val message: String) : Failure

    public data class NotFoundFailure(override val message: String) : Failure

    public data class AlreadyExistFailure(override val message: String) : Failure

    public data class GenericFailure(val e: Throwable) : Failure {
        override val message: String = e.message ?: "Unknown error occurred"
    }
}

public fun <T> eval(logger: Logger, exec: () -> T): Either<Failure, T> {
    return Either.catch {
        exec()
    }.mapLeft {
        logger.error("Failed to execute operation because of - ${it.message}")
        Failure.GenericFailure(it)
    }
}
