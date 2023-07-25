package com.poisonedyouth.financemanagement.user.port

public data class UserDto(
    val userId: String,
    val firstname: String,
    val lastname: String,
    val email: String
)

public data class NewUserDto(
    val firstname: String,
    val lastname: String,
    val email: String
)
