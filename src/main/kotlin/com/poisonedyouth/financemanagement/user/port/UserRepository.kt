package com.poisonedyouth.financemanagement.user.port

import arrow.core.Either
import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.user.domain.Email
import com.poisonedyouth.financemanagement.user.domain.NewUser
import com.poisonedyouth.financemanagement.user.domain.User
import java.util.UUID

public interface UserRepository {
    public fun create(user: NewUser): Either<Failure, User>
    public fun update(user: User): Either<Failure, User>
    public fun delete(userId: UUID): Either<Failure, Int>
    public fun findById(userId: UUID): Either<Failure, User?>
    public fun findByEmail(email: Email): Either<Failure, User?>
}
