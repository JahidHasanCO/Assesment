package dev.jahidhasanco.assesment.utils.networkState

import dev.jahidhasanco.assesment.data.model.User

data class UserDataState(
    val data: List<User>? = null,
    val error: String = "",
    val isLoading: Boolean = false
)