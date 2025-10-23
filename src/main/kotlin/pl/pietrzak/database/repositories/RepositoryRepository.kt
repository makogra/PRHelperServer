package pl.pietrzak.database.repositories

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import pl.pietrzak.database.tables.Repositories
import pl.pietrzak.database.tables.RepositoryReviewers

class RepositoryRepository {
    fun create(name: String, portalName: String, url: String, creatorId: Int): Int {
        return transaction {
            Repositories.insertAndGetId {
                it[Repositories.name] = name
                it[Repositories.portalName] = portalName
                it[Repositories.url] = url
                it[Repositories.creator] = creatorId
                it[Repositories.createdAt] = Clock.System.now()
            }.value
        }
    }

    fun addReviewer(repositoryId: Int, userId: Int) = transaction {
        RepositoryReviewers.insert {
            it[repository] = repositoryId
            it[user] = userId
        }
    }
}