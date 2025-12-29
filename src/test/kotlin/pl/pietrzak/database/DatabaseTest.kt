package pl.pietrzak.pl.pietrzak.database

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import pl.pietrzak.database.dao.RecommendationEntity
import pl.pietrzak.database.tables.Conversations
import pl.pietrzak.database.tables.Recommendations
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class DatabaseTest {

    private val insertedRecommendationIds = mutableListOf<EntityID<Int>>()
    private val insertedConversationIds = mutableListOf<EntityID<Int>>()

    @BeforeTest
    fun setup() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/prhelperdb",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )
        //TODO move it to profile and maybe secrets, as for now (localhost) it can be as is

        transaction {
            createMissingTablesAndColumns(Conversations, Recommendations)
        }
    }

    @AfterTest
    fun teardown() {
        transaction {
            if (insertedRecommendationIds.isNotEmpty()) {
                Recommendations.deleteWhere { Recommendations.id inList insertedRecommendationIds.map { it.value } }
                println("Deleted ${insertedRecommendationIds.size} recommendations created during the test.")
            }

            if (insertedConversationIds.isNotEmpty()) {
                Conversations.deleteWhere { Conversations.id inList insertedConversationIds.map { it.value } }
                println("Deleted ${insertedConversationIds.size} conversations created during the test.")
            }

            insertedRecommendationIds.clear()
            insertedConversationIds.clear()
        }
    }

    @Test
    fun testDatabaseConnectionAndInsert() {
        transaction {
            createMissingTablesAndColumns(Conversations, Recommendations)

            val recommendationEntity = RecommendationEntity.new {
                category = "Code Structure"
                level = 2
                change = "Refactor route handling by separating concerns into dedicated route files."
                comment = "The separation of OAuth and webhook routes into their own files helps to maintain cleaner and more organized code."
            }
            insertedRecommendationIds += recommendationEntity.id

            val fetched = RecommendationEntity.findById(recommendationEntity.id)
            assertNotNull(fetched)
            assertEquals("Code Structure", fetched.category)

            println("Inserted Recommendation: ${fetched.category} | ${fetched.comment.take(60)}...")
        }
    }

}