package com.poisonedyouth.financemanagement.failure

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.types.shouldBeTypeOf
import org.slf4j.LoggerFactory

class FailureTest : AnnotationSpec() {

    private val logger = LoggerFactory.getLogger(FailureTest::class.java)

    @Test
    fun `eval returns result of function call when successful`() {
        // given
        val function = { 8 / 2 }

        // when
        val actual = eval(logger) {
            function.invoke()
        }

        // then
        actual shouldBeRight 4
    }

    @Test
    fun `eval returns failure when function throws an exception`() {
        // given
        val function = { 8 / 0 }

        // when
        val actual = eval(logger) {
            function.invoke()
        }

        // then
        actual.shouldBeLeft().shouldBeTypeOf<Failure.GenericFailure>()
    }
}
