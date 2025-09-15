package pl.pietrzak.entity.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    val id: Long,
    val name: String,
    val owner: User,
    @SerialName("commits_url") val commitsUrl: String,
    @SerialName("pulls_url") val pullsUrl: String,
)
