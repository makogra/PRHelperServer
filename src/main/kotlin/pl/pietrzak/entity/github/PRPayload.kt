package pl.pietrzak.entity.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PRPayload(
    val action: String,
    @SerialName("pull_request") val pullRequest: PullRequest,
    val repository: Repository
)
