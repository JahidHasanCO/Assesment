package dev.jahidhasanco.assesment.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val city: String = "",
    val skill: List<String> = listOf(),
    val dateOfBirth: String = "",
    val resume: String = "",
    val resumeTitle: String = ""
)
