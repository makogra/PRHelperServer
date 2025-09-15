package pl.pietrzak.entity.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequest (
    val url: String,
    val id: Long,
    @SerialName("node_id") val nodeId: String,
    @SerialName("diff_url") val diffUrl: String,
    val number: Long,
    val state: String,
//    val status: Boolean,//?
    val title: String,
    val user: User,
    val body: String?,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("closed_at") val closedAt: String?,
    @SerialName("commits_url") val commitsUrl: String,
)