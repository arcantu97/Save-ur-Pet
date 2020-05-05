package arcan.apps.saveurpet.models

class UserModel {
    data class User(
        var adminPermission: Int,
        var username: String? = "",
        var email: String? = "",
        var uid: String? = "",
        var address: String? = "",
        var state: String? = "",
        var phone1: String? = "",
        var phone2: String? = "")
}