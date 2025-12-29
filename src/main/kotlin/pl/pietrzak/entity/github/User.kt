package pl.pietrzak.entity.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val id: Int,
    @SerialName("avatar_url") val avatarUrl: String?,
)
