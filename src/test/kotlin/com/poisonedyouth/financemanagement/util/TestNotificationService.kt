package com.poisonedyouth.financemanagement.util

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.financemanagement.account.port.Notification
import com.poisonedyouth.financemanagement.account.port.NotificationService
import com.poisonedyouth.financemanagement.failure.Failure

class TestNotificationService : NotificationService {
    override fun notify(notification: Notification): Either<Failure, Unit> = either {
        println("Notify $notification")
    }
}
