package com.poisonedyouth.financemanagement.user.service

import com.poisonedyouth.financemanagement.account.port.AccountUseCase
import com.poisonedyouth.financemanagement.account.port.NotificationService
import com.poisonedyouth.financemanagement.account.service.AccountService
import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.user.port.NewUserDto
import com.poisonedyouth.financemanagement.user.port.UserDto
import com.poisonedyouth.financemanagement.user.port.UserRepository
import com.poisonedyouth.financemanagement.user.port.UserUseCase
import com.poisonedyouth.financemanagement.util.TestNotificationService
import com.poisonedyouth.financemanagement.util.TestUserRepository
import com.poisonedyouth.financemanagement.util.defaultUser
import com.poisonedyouth.financemanagement.util.defaultUserEmail
import com.poisonedyouth.financemanagement.util.defaultUserId
import com.poisonedyouth.financemanagement.util.duplicateUserEmail
import com.poisonedyouth.financemanagement.util.notExistingUserId
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import java.util.UUID

class UserServiceTest : AnnotationSpec() {

    private val userRepository: UserRepository = TestUserRepository()
    private val notificationService: NotificationService = TestNotificationService()
    private val accountUseCase: AccountUseCase = AccountService(notificationService)
    private val userService: UserUseCase = UserService(userRepository, accountUseCase)

    @Test
    fun `create returns failure when user cannot be persisted`() {
        // given
        val userDto = NewUserDto(
            firstname = "Max",
            lastname = "Doe",
            email = "max.doe@mail.com"
        )

        // when
        val actual = userService.create(userDto)

        // then
        val failure = actual.shouldBeLeft()
        failure.message shouldBe "Cannot persist 'max.doe@mail.com'"
    }

    @Test
    fun `create returns persisted user when persistence is successful`() {
        // given
        val userDto = NewUserDto(
            firstname = "Max",
            lastname = "Doe",
            email = defaultUserEmail.value
        )

        // when
        val actual = userService.create(userDto)

        // then
        val userId = actual.shouldBeRight()
        userId shouldBe defaultUserId.toString()
    }

    @Test
    fun `create returns failure when email already exists`() {
        // given
        val userDto = NewUserDto(
            firstname = "Max",
            lastname = "Doe",
            email = duplicateUserEmail.value
        )

        // when
        val actual = userService.create(userDto)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.AlreadyExistFailure>()
        failure.message shouldBe "User with email '${duplicateUserEmail.value}' already exists."
    }

    @Test
    fun `update returns failure when user cannot be persisted`() {
        // given
        val userDto = UserDto(
            userId = defaultUserId.toString(),
            firstname = "Max",
            lastname = "Doe",
            email = "max.doe@mail.com"
        )

        // when
        val actual = userService.update(userDto)

        // then
        val failure = actual.shouldBeLeft()
        failure.message shouldBe "Cannot update 'max.doe@mail.com'"
    }

    @Test
    fun `update returns updated user when persistence is successful`() {
        // given
        val userDto = UserDto(
            userId = defaultUserId.toString(),
            firstname = "Max",
            lastname = "Doe",
            email = defaultUserEmail.value
        )

        // when
        val actual = userService.update(userDto)

        // then
        actual.shouldBeRight()
    }

    @Test
    fun `delete returns failure when given userId is no valid UUID`() {
        // given
        val userId = "INVALID"

        // when
        val actual = userService.delete(userId)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.ValidationFailure>()
        failure.message shouldBe "Invalid UUID string: INVALID"
    }

    @Test
    fun `delete returns failure when deletion in database fails`() {
        // given
        val userId = UUID.randomUUID().toString()

        // when
        val actual = userService.delete(userId)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.GenericFailure>()
        failure.message shouldBe "Cannot delete '$userId'"
    }

    @Test
    fun `delete returns amount of deleted user when deletion is successful`() {
        // given
        val userId = defaultUserId.toString()

        // when
        val actual = userService.delete(userId)

        // then
        val amount = actual.shouldBeRight()
        amount shouldBe 1
    }

    @Test
    fun `delete returns failure when user does not exist in database`() {
        // given
        val userId = notExistingUserId.toString()

        // when
        val actual = userService.delete(userId)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.NotFoundFailure>()
        failure.message shouldBe "User with id '95d374d2-440c-4db7-8db5-9f70e31b415e' does not exist."
    }

    @Test
    fun `findById returns failure when given userId is no valid UUID`() {
        // given
        val userId = "INVALID"

        // when
        val actual = userService.findById(userId)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.ValidationFailure>()
        failure.message shouldBe "Invalid UUID string: INVALID"
    }

    @Test
    fun `findById returns null when given user does not exist`() {
        // given
        val userId = notExistingUserId.toString()

        // when
        val actual = userService.findById(userId)

        // then
        val existingUser = actual.shouldBeRight()
        existingUser shouldBe null
    }

    @Test
    fun `findById returns userDto when given user exist`() {
        // given
        val userId = defaultUserId.toString()

        // when
        val actual = userService.findById(userId)

        // then
        val existingUser = actual.shouldBeRight()
        existingUser shouldBe UserDto(
            userId = defaultUserId.toString(),
            firstname = defaultUser.firstname.value,
            lastname = defaultUser.lastname.value,
            email = defaultUser.email.value
        )
    }
}
