package com.bachnn.messenger.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
class User(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "photoUrl") val photoUrl: String,
    @ColumnInfo(name = "emailVerified") val emailVerified: String,
    @ColumnInfo(name = "pushToken") val token: String,
    val openTime: String = "",
    var numberUnread: String = ""
) : java.io.Serializable {

    override fun toString(): String {
        return "User(uid='$uid', name='$name', email='$email', photoUrl='$photoUrl', emailVerified='$emailVerified', token='$token', openTime='$openTime', numberUnread='$numberUnread')"
    }
}
