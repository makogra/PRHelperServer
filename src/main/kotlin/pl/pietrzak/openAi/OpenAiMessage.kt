package pl.pietrzak.openAi

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    val temperature: Double
)

@Serializable
data class OpenAiResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val usage: UsageDetails,
    val choices: List<Choice>
) {
    @Serializable
    data class UsageDetails(
        val prompt_tokens: Int,
        val completion_tokens: Int,
        val total_tokens: Int,
        val completion_tokens_details: CompletionTokensDetails
    )

    @Serializable
    data class CompletionTokensDetails(
        val reasoning_tokens: Int,
        val accepted_prediction_tokens: Int,
        val rejected_prediction_tokens: Int
    )

    @Serializable
    data class Choice(
        val message: Message,
        val logprobs: String?,
        val finish_reason: String,
        val index: Int
    ) {
        @Serializable
        data class Message(
            val role: String,
            val content: String
        )
    }
}