package pl.pietrzak.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Table.Dual.reference

object RepositoryReviewers : Table("repository_reviewers") {
    val repository = reference("repository_id", Repositories)
    val user = reference("user_id", Users)

    override val primaryKey = PrimaryKey(repository, user, name = "PK_RepositoryReviewer")
}