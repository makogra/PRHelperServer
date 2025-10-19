package pl.pietrzak.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Recommendations : IntIdTable("recommendations") {
    val conversation = reference("conversation_id", Conversations)
    val category = varchar("category", 255).nullable()
    val level = integer("level")
    val change = text("change")
    val comment = text("comment")
//    val createdAt = timestamp("created_at").defaultExpression(())
}