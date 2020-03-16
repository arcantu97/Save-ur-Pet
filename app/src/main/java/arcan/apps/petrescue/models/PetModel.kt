package arcan.apps.petrescue.models

class PetModel {
    data class Pet(val petName: String, val petImageURL: String, val Adopted: Boolean, val Rescued: Boolean, val entryDate: String, val deathDate: String)
}