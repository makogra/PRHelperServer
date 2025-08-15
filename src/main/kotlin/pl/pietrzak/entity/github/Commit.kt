package pl.pietrzak.entity.github

import kotlinx.serialization.SerialName

data class Commit(
    val sha: String,
    @SerialName("node_id") val nodeId: String,
)
