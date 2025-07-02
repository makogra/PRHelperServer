package pl.pietrzak.auth

import kotlinx.serialization.Serializable

@Serializable
data class GitHubUser(
    val login: String,
    val id: Long,
    val name: String? = null,
    val avatar_url: String? = null,
    val email: String? = null
)
