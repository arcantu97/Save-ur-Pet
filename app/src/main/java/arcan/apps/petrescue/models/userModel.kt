package arcan.apps.petrescue.models

class UserModel {
    data class User(val adminPermission: Int, val username: String, val email: String, val uid: String)
}