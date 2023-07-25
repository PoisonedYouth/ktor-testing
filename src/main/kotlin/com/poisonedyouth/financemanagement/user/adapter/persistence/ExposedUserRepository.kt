package com.poisonedyouth.financemanagement.user.adapter.persistence

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.poisonedyouth.financemanagement.common.Identity
import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.failure.eval
import com.poisonedyouth.financemanagement.user.domain.Email
import com.poisonedyouth.financemanagement.user.domain.Name
import com.poisonedyouth.financemanagement.user.domain.NewUser
import com.poisonedyouth.financemanagement.user.domain.User
import com.poisonedyouth.financemanagement.user.port.UserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.util.UUID

public class ExposedUserRepository : UserRepository {
    private val logger = LoggerFactory.getLogger(ExposedUserRepository::class.java)

    override fun create(user: NewUser): Either<Failure, User> = transaction {
        eval(logger) {
            val id = UserTable.insertAndGetId {
                it[firstname] = user.firstname.value
                it[lastname] = user.lastname.value
                it[email] = user.email.value
            }
            User(
                userId = Identity(id.value),
                firstname = user.firstname,
                lastname = user.lastname,
                email = user.email
            )
        }
    }

    override fun update(user: User): Either<Failure, User> = transaction {
        val updateResult = eval(logger) {
            UserTable.update({ UserTable.id eq user.userId.id }) {
                it[firstname] = user.firstname.value
                it[lastname] = user.lastname.value
                it[email] = user.email.value
            }
        }
        either {
            ensure(updateResult.bind() == 1) {
                Failure.NotFoundFailure("The user with email '${user.email.value}' does not exist.")
            }
            user
        }
    }

    override fun delete(userId: UUID): Either<Failure, Int> = transaction {
        eval(logger) {
            UserTable.deleteWhere { UserTable.id eq userId }
        }
    }

    override fun findById(userId: UUID): Either<Failure, User?> = transaction {
        either {
            eval(logger) {
                UserTable.select { UserTable.id eq userId }.firstOrNull()
            }.bind()?.let {
                User(
                    userId = Identity(it[UserTable.id].value),
                    firstname = Name.from(it[UserTable.firstname]).bind(),
                    lastname = Name.from(it[UserTable.lastname]).bind(),
                    email = Email.from(it[UserTable.email]).mapLeft { Failure.ValidationFailure(it.message) }.bind()
                )
            }
        }
    }

    override fun findByEmail(email: Email): Either<Failure, User?> = transaction {
        either {
            eval(logger) {
                UserTable.select { UserTable.email eq email.value }.firstOrNull()
            }.bind()?.let {
                User(
                    userId = Identity(it[UserTable.id].value),
                    firstname = Name.from(it[UserTable.firstname]).bind(),
                    lastname = Name.from(it[UserTable.lastname]).bind(),
                    email = Email.from(it[UserTable.email]).mapLeft { Failure.ValidationFailure(it.message) }.bind()
                )
            }
        }
    }
}
