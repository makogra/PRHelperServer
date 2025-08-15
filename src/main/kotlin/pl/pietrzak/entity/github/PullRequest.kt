package pl.pietrzak.entity.github

import kotlinx.serialization.SerialName

data class PullRequest (
    val url: String,
    val id: String,
    @SerialName("node_id") val nodeId: String,
    val number: Long,
    val state: String,
    val status: Boolean,
    val title: String,
    val user: User,
    val body: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("commits_url") val commitsUrl: String,
)