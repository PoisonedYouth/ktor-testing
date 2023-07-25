package com.poisonedyouth.financemanagement.account.port

import arrow.core.Either
import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.user.domain.Email

public interface NotificationService {

    public fun notify(notification: Notification): Either<Failure, Unit>
}

public sealed interface Notification {

    public data class EmailNotification(
        val email: Email
    ) : Notification
}
