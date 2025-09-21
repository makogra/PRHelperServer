package pl.pietrzak.database.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Users : UUIDTable("users") {
    val login = varchar("login", 255).uniqueIndex() // GitHub login or GitLab username
    val portalName = varchar("portal_name", 50) // e.g. github, gitlab
    val iconUrl = varchar("icon_url", 512).nullable()
    val createdAt = timestamp("created_at")
}