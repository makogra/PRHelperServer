package pl.pietrzak.openAi

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationsResponse(
    val recommendations: List<Recommendation>
)

@Serializable
data class Recommendation(
    val category: String,
    val level: Int,
    val change: String,
    val comment: String
)
