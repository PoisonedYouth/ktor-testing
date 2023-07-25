package com.poisonedyouth.financemanagement.account.service

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.financemanagement.account.port.AccountUseCase
import com.poisonedyouth.financemanagement.account.port.Notification
import com.poisonedyouth.financemanagement.account.port.NotificationService
import com.poisonedyouth.financemanagement.failure.Failure
import com.poisonedyouth.financemanagement.user.domain.Email

public class AccountService(
    private val notificationService: NotificationService
) : AccountUseCase {
    override fun triggerCreation(email: Email): Either<Failure, Unit> = either {
        notificationService.notify(Notification.EmailNotification(email))
    }
}
