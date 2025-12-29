package pl.pietrzak.database.repositories

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.pietrzak.database.tables.Users
import java.util.*

class UserRepository {
    fun create(login: String, portalName: String, iconUrl: String?): Int {
        return transaction {
            Users.insertAndGetId {
                it[Users.login] = login
                it[Users.portalName] = portalName
                it[Users.iconUrl] = iconUrl
                it[Users.createdAt] = Clock.System.now()
            }.value
        }
    }

    fun findByLogin(login: String, portalName: String) = transaction {
        Users.selectAll().where { (Users.login eq login) and (Users.portalName eq portalName) }.singleOrNull()
    }
}