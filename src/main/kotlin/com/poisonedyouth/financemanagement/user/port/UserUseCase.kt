package com.poisonedyouth.financemanagement.user.port

import arrow.core.Either
import com.poisonedyouth.financemanagement.failure.Failure

public interface UserUseCase {
    public fun create(userDto: NewUserDto): Either<Failure, String>
    public fun update(userDto: UserDto): Either<Failure, Unit>
    public fun delete(userId: String): Either<Failure, Int>
    public fun findById(userId: String): Either<Failure, UserDto?>
}
