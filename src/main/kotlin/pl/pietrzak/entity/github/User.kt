package pl.pietrzak.entity.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val id: Long,
    val name: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val email: String? = null
)
