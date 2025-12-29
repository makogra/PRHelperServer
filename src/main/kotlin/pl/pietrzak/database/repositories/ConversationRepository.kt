package pl.pietrzak.database.repositories

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.pietrzak.database.tables.Conversations

class ConversationRepository {
    fun save(userId: String, prompt: String, response: String) {
        transaction {
            Conversations.insert {
                it[Conversations.userId] = userId
                it[Conversations.prompt] = prompt
                it[Conversations.response] = response
                it[Conversations.createdAt] = Clock.System.now()
            }
        }
    }

    fun getByUser(userId: String): List<Pair<String, String>> {
        return transaction {
            Conversations.selectAll().where { Conversations.userId eq userId }
                .map { it[Conversations.prompt] to it[Conversations.response] }
        }
    }
}