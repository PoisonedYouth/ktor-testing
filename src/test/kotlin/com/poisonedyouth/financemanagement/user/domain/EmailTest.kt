package com.poisonedyouth.financemanagement.user.domain

import arrow.exact.ExactError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class EmailTest : AnnotationSpec() {

    @Test
    fun `create Email with invalid value returns failure`() {
        // given
        val emailString = "notvalid"

        // when
        val actual = Email.from(emailString)

        // then
        val failure = actual.shouldBeLeft().shouldBeTypeOf<ExactError>()
        failure.message shouldBe "Email 'notvalid' is no valid mail address."
    }

    @Test
    fun `create Email with valid value returns instance`() {
        // given
        val emailString = "john.doe@mail.com"

        // when
        val actual = Email.from(emailString)

        // then
        val email = actual.shouldBeRight()
        email.value shouldBe emailString
    }
}
