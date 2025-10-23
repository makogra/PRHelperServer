package pl.pietrzak.database.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pl.pietrzak.database.tables.Recommendations
import pl.pietrzak.openAi.Recommendation

class RecommendationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RecommendationEntity>(Recommendations)

//    var conversationEntity by ConversationEntity referencedOn Recommendations.conversation
    var category by Recommendations.category
    var level by Recommendations.level
    var change by Recommendations.change
    var comment by Recommendations.comment
//    var createdAt by Recommendations.createdAt

    fun toRecommendation() = Recommendation(category, level, change, comment)
}