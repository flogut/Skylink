package de.hgv.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class User(username: String? = null, password: String? = null) {
    val usernameProperty = SimpleStringProperty(username)
    var username by usernameProperty

    val passwordProperty = SimpleStringProperty(password)
    var password by passwordProperty
}

class UserModel(user: User): ItemViewModel<User>(user) {
    val password = bind(User::passwordProperty)
    val username = bind(User::usernameProperty)
}

