package dev.jahidhasanco.assesment.utils.temp

import dev.jahidhasanco.assesment.data.model.User

object UserTempData {
    private var user: User? = null

    fun clear() {
        user = null
    }

    fun addUser(user: User) {
        this.user = user
    }

    fun getUser() = user
}