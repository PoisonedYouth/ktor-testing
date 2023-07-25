package com.poisonedyouth.financemanagement.common

import arrow.core.Either
import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.failure.eval
import org.slf4j.LoggerFactory
import java.util.UUID

private val logger = LoggerFactory.getLogger(Identity::class.java)

@JvmInline
public value class Identity(public val id: UUID) {
    public companion object {
        public fun resolveFromString(value: String): Either<Failure, Identity> {
            return eval(logger) {
                val uuid = UUID.fromString(value)
                Identity(uuid)
            }.mapLeft {
                Failure.ValidationFailure(it.message)
            }
        }
    }
}
