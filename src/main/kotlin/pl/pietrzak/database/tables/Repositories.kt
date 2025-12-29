package pl.pietrzak.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Repositories : IntIdTable("repositories") {
    val portalName = varchar("portal_name", 50) // e.g. github, gitlab
    val name = varchar("name", 255) // repository name
    val url = varchar("url", 512)
    val creator = reference("creator_id", Users, onDelete = ReferenceOption.CASCADE)
    val createdAt = timestamp("created_at")
}