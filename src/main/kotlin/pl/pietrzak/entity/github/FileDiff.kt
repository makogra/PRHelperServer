package pl.pietrzak.entity.github

import kotlinx.serialization.SerialName

data class FileDiff(
    val sha: String,
    @SerialName("filename") val fileName: String,
    val path: String,
    )