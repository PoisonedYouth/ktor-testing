package com.poisonedyouth.financemanagement.user.domain

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.exact.Exact
import arrow.exact.ExactError
import java.util.regex.Pattern

private val validEmailPattern = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

@JvmInline
public value class Email private constructor(public val value: String) {
    public companion object : Exact<String, Email> {
        override fun Raise<ExactError>.spec(raw: String): Email {
            ensureWithError(validEmailPattern.matcher(raw).matches()) {
                ExactError("Email '$raw' is no valid mail address.")
            }
            return Email(raw)
        }
    }
}

@RaiseDSL
public inline fun Raise<ExactError>.ensureWithError(condition: Boolean, raise: () -> ExactError) {
    if (!condition) raise(raise())
}