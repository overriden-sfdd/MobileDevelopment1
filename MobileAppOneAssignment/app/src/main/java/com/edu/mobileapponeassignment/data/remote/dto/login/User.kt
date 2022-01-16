package com.edu.mobileapponeassignment.data.remote.dto.login

import com.edu.mobileapponeassignment.data.remote.dto.location.Location

data class User(
    val id: String,
    val title: String,
    var firstName: String,
    val lastName: String,
    val gender: String,
    val email: String,
    val dateOfBirth: String,
    val registerDate: String,
    val phone: String,
    val picture: String,
    val location: Location,
)

fun User.toRealUser(firstname: String): User {
    firstName = firstname
    return this
}
