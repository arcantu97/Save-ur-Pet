package arcan.apps.petrescue.models

class UserModel {
    data class User(
        var adminPermission: Int,
        var username: String? = "",
        var email: String? = "",
        var uid: String? = "")
}