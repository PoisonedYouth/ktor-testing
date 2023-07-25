package com.poisonedyouth.financemanagement.user.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.poisonedyouth.financemanagement.account.port.AccountUseCase
import com.poisonedyouth.financemanagement.common.Identity
import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.failure.eval
import com.poisonedyouth.financemanagement.user.domain.Email
import com.poisonedyouth.financemanagement.user.domain.Name
import com.poisonedyouth.financemanagement.user.domain.NewUser
import com.poisonedyouth.financemanagement.user.domain.User
import com.poisonedyouth.financemanagement.user.port.NewUserDto
import com.poisonedyouth.financemanagement.user.port.UserDto
import com.poisonedyouth.financemanagement.user.port.UserRepository
import com.poisonedyouth.financemanagement.user.port.UserUseCase
import org.slf4j.LoggerFactory
import java.util.UUID

public class UserService(
    private val userRepository: UserRepository,
    private val accountUseCase: AccountUseCase
) : UserUseCase {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    override fun create(userDto: NewUserDto): Either<Failure, String> = either {
        val user = validateAndCreateNewUser(userDto).bind()
        val email = user.email
        val existingUser = userRepository.findByEmail(email).bind()
        ensure(existingUser == null) {
            Failure.AlreadyExistFailure("User with email '${email.value}' already exists.")
        }

        val persistedUser = userRepository.create(user).bind()
        accountUseCase.triggerCreation(persistedUser.email)
        persistedUser.userId.id.toString()
    }

    override fun update(userDto: UserDto): Either<Failure, Unit> = either {
        val user = validateAndCreateUser(userDto)
        userRepository.update(user.bind()).bind()
        Unit
    }

    override fun delete(userId: String): Either<Failure, Int> = either {
        val existingUserId = mapToUUID(userId).bind()
        val deletedAmount = userRepository.delete(existingUserId).bind()
        ensure(deletedAmount != 0) {
            Failure.NotFoundFailure("User with id '$userId' does not exist.")
        }
        deletedAmount
    }

    override fun findById(userId: String): Either<Failure, UserDto?> = either {
        val existingUserId = mapToUUID(userId).bind()
        userRepository.findById(existingUserId).bind()?.toUserDto()?.bind()
    }

    private fun mapToUUID(userId: String) = eval(logger) {
        UUID.fromString(userId)
    }.mapLeft { Failure.ValidationFailure(it.message) }

    private fun validateAndCreateUser(userDto: UserDto): Either<Failure, User> = either {
        User(
            userId = Identity.resolveFromString(userDto.userId).bind(),
            firstname = Name.from(userDto.firstname).bind(),
            lastname = Name.from(userDto.lastname).bind(),
            email = Email.from(userDto.email).mapLeft { Failure.ValidationFailure(it.message) }.bind()
        )
    }

    private fun validateAndCreateNewUser(userDto: NewUserDto): Either<Failure, NewUser> = either {
        NewUser(
            firstname = Name.from(userDto.firstname).bind(),
            lastname = Name.from(userDto.lastname).bind(),
            email = Email.from(userDto.email).mapLeft { Failure.ValidationFailure(it.message) }.bind()
        )
    }

    private fun User.toUserDto(): Either<Failure, UserDto> = either {
        UserDto(
            userId = this@toUserDto.userId.id.toString(),
            firstname = this@toUserDto.firstname.value,
            lastname = this@toUserDto.lastname.value,
            email = this@toUserDto.email.value
        )
    }
}
