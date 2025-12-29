package pl.pietrzak.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Conversations : IntIdTable("conversations") {
    val userId = varchar("user_id", 255) // could be GitHub user login or UUID
    val prompt = text("prompt")
    val response = text("response")
    val createdAt = timestamp("created_at")
}