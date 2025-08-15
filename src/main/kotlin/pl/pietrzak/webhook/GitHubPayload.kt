package pl.pietrzak.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubPullRequestPayload(
    val action: String,
    @SerialName("pull_request") val pullRequest: PullRequest,
    val repository: Repository
)

@Serializable
data class PullRequest(
    val url: String,
    val id: Long,
    val diffUrl: String,
    val head: Head,
    val base: Base
)

@Serializable
data class Head(
    val ref: String,
    val sha: String
)

@Serializable
data class Base(
    val ref: String,
    val sha: String
)

@Serializable
data class Repository(
    val name: String,
    val owner: Owner
)

@Serializable
data class Owner(
    val login: String
)
