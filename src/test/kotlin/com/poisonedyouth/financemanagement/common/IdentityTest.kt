package com.poisonedyouth.financemanagement.common

import com.poisonedyouth.financemanagement.failure.Failure
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import java.util.UUID

class IdentityTest : AnnotationSpec() {

    @Test
    fun `resolveFromString returns failure for invalid UUID`() {
        // given
        val uuid = "INVALID"

        // when
        val actual = Identity.resolveFromString(uuid)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.ValidationFailure>()
        failure.message shouldBe "Invalid UUID string: INVALID"
    }

    @Test
    fun `resolveFromString returns Identity object for valid UUID`() {
        // given
        val uuid = UUID.randomUUID().toString()

        // when
        val actual = Identity.resolveFromString(uuid)

        // then
        val uuidIdentity = actual.shouldBeRight()
        uuidIdentity.id.toString() shouldBe uuid
    }
}
