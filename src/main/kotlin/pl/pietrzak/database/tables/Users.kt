package pl.pietrzak.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Users : IntIdTable("users") {
    val login = varchar("login", 255).uniqueIndex()
    //TODO add enum type
    val portalName = varchar("portal_name", 50)
    val iconUrl = varchar("icon_url", 512).nullable()
    val createdAt = timestamp("created_at")
}