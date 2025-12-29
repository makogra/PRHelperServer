package pl.pietrzak.database.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pl.pietrzak.database.tables.Conversations

class ConversationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ConversationEntity>(Conversations)

    var userId by Conversations.userId
    var prompt by Conversations.prompt
    var createdAt by Conversations.createdAt
//    val recommendations by RecommendationEntity referrersOn Recommendations.conversation

//    fun toConversation() = Conversation
}