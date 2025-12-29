package pl.pietrzak.database.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pl.pietrzak.database.tables.Users
import pl.pietrzak.entity.github.User

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var login by Users.login
    var portalName by Users.portalName
    var iconUrl by Users.iconUrl
    var createdAt by Users.createdAt

    fun toUser() = User(login,id.value,iconUrl)
}