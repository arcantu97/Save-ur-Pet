package arcan.apps.petrescue.models

class PetModel {
    data class Pet(
        var petName: String? = "",
        var petImageURL: String? = "",
        var adoptBy: String? = "",
        var rescuedBy: String? = "",
        var adoptDate: String? = "",
        var rescuedDate: String? = "",
        var visitDate: String? = "",
        var entryDate: MutableMap<String, String>,
        var deathDate: Long,
        var adopted: Boolean?,
        var rescued: Boolean?,
        var requestAdoption: Boolean?,
        var requestRescue: Boolean?,
        var NonRequested: Boolean?)
}