package com.poisonedyouth.financemanagement.user.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.poisonedyouth.financemanagement.failure.Failure

public const val NAME_MIN_LENGTH: Int = 3
public const val NAME_MAX_LENGTH: Int = 20

@JvmInline
public value class Name private constructor(public val value: String) {
    public companion object {

        public fun from(rawString: String): Either<Failure, Name> = either {
            ensure(rawString.length in NAME_MIN_LENGTH..NAME_MAX_LENGTH) {
                Failure.ValidationFailure(
                    "Name length must be between $NAME_MIN_LENGTH and $NAME_MAX_LENGTH but is ${rawString.length}"
                )
            }
            Name(rawString)
        }
    }
}
