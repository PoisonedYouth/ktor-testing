package com.poisonedyouth.financemanagement.user.domain

import com.poisonedyouth.financemanagement.failure.Failure
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class NameTest : AnnotationSpec() {

    @Test
    fun `creating Name with empty string returns failure`() {
        // given
        val nameString = ""

        // when
        val actual = Name.from(nameString)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.ValidationFailure>()
        failure.message shouldBe "Name length must be between 3 and 20 but is 0"
    }

    @Test
    fun `creating Name with too long value returns failure`() {
        // given
        val nameString = "123456789012345678901"

        // when
        val actual = Name.from(nameString)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<Failure.ValidationFailure>()
        failure.message shouldBe "Name length must be between 3 and 20 but is 21"
    }

    @Test
    fun `creating Name with valid value returns instance`() {
        // given
        val nameString = "John"

        // when
        val actual = Name.from(nameString)

        // then
        val name = actual.shouldBeRight()
        name.value shouldBe ("John")
    }
}
