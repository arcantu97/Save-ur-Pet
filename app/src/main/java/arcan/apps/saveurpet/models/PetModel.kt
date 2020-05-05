package arcan.apps.saveurpet.models

import com.google.firebase.firestore.FieldValue

class PetModel {
    data class Pet(
        var petName: String? = "",
        var petImageURL: String? = "",
        var adoptBy: String? = "",
        var rescuedBy: String? = "",
        var adoptDate: String? = "",
        var rescuedDate: String? = "",
        var visitDate: String? = "",
        var entryDate: Long,
        var deathDate: Long,
        var adopted: Boolean?,
        var rescued: Boolean?,
        var requestAdoption: Boolean?,
        var requestRescue: Boolean?,
        var NonRequested: Boolean?,
        var municity: String?)
}