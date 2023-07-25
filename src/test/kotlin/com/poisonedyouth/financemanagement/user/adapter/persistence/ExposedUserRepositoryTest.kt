package com.poisonedyouth.financemanagement.user.adapter.persistence

import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.user.domain.Email
import com.poisonedyouth.financemanagement.util.createRandomDefaultNewUser
import com.poisonedyouth.financemanagement.util.createRandomDefaultUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeTypeOf
import org.jetbrains.exposed.sql.Database
import java.util.UUID

class ExposedUserRepositoryTest : AnnotationSpec() {

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        UserTable.initTable()
    }

    private val userRepository = ExposedUserRepository()

    @Test
    fun `create adds new user to database`() {
        // given
        val user = createRandomDefaultNewUser()

        // when
        val actual = userRepository.create(user)

        // then
        val persistedUser = actual.shouldBeRight()
        persistedUser.userId shouldNotBe null
        persistedUser.firstname shouldBe user.firstname
        persistedUser.lastname shouldBe user.lastname
        persistedUser.email shouldBe user.email
        userRepository.findById(persistedUser.userId.id).shouldBeRight(persistedUser)
    }

    @Test
    fun `create returns failure when email already exists`() {
        // given
        val user = createRandomDefaultNewUser()
        userRepository.create(user)

        // when
        val actual = userRepository.create(user)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.GenericFailure>()
        failure.message shouldContain "JdbcSQLIntegrityConstraintViolationException"
    }

    @Test
    fun `update changes existing user in database`() {
        // given
        val user = createRandomDefaultNewUser()
        val existingUser = userRepository.create(user).shouldBeRight()

        // when
        val actual = userRepository.update(
            existingUser.copy(
                email = Email.from("John.Doe@yahoo.com").shouldBeRight()
            )
        )

        // then
        val updatedUser = actual.shouldBeRight()
        updatedUser.userId shouldBe existingUser.userId
        updatedUser.email shouldNotBe existingUser.email
        userRepository.findById(updatedUser.userId.id).shouldBeRight(updatedUser)
    }

    @Test
    fun `update returns failure when user does not exist`() {
        // given
        val user = createRandomDefaultUser(UUID.randomUUID())

        // when
        val actual = userRepository.update(user)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.NotFoundFailure>()
        failure.message shouldBe "The user with email '${user.email.value}' does not exist."
    }

    @Test
    fun `delete returns zero when user does not exist`() {
        // given
        val user = createRandomDefaultNewUser()
        userRepository.create(user)

        // when
        val actual = userRepository.delete(UUID.randomUUID())

        // then
        actual.shouldBeRight(0)
    }

    @Test
    fun `delete returns one when user exists`() {
        // given
        val user = createRandomDefaultNewUser()
        val persistedUser = userRepository.create(user).shouldBeRight()

        // when
        val actual = userRepository.delete(persistedUser.userId.id)

        // then
        actual.shouldBeRight(1)
    }

    @Test
    fun `findById returns null when user does not exist`() {
        // given
        val user = createRandomDefaultNewUser()
        userRepository.create(user).shouldBeRight()

        // when
        val actual = userRepository.findById(UUID.randomUUID())

        // then
        actual.shouldBeRight(null)
    }

    @Test
    fun `findById returns matching user when exists`() {
        // given
        val user = createRandomDefaultNewUser()
        val persistedUser = userRepository.create(user).shouldBeRight()

        // when
        val actual = userRepository.findById(persistedUser.userId.id)

        // then
        actual.shouldBeRight(persistedUser)
    }

    @Test
    fun `findByEmail returns matching user when exists`() {
        // given
        val user = createRandomDefaultNewUser()
        val persistedUser = userRepository.create(user).shouldBeRight()

        // when
        val actual = userRepository.findByEmail(persistedUser.email)

        // then
        actual.shouldBeRight(persistedUser)
    }

    @Test
    fun `findByEmail returns null when user does not exist`() {
        // given
        val user = createRandomDefaultNewUser()
        userRepository.create(user).shouldBeRight()

        // when
        val actual = userRepository.findByEmail(Email.from("notExisting@mail.com").getOrNull()!!)

        // then
        actual.shouldBeRight(null)
    }
}
