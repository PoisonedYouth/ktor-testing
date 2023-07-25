package com.poisonedyouth.financemanagement.account.domain

import com.poisonedyouth.financemanagement.common.Identity

public data class Account(
    val id: Identity,
    val name: String
)

public data class NewAccount(
    val name: String
)
