package arcan.apps.petrescue

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import arcan.apps.petrescue.models.PetModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.Period
import java.time.temporal.ChronoUnit


class RegisterActivity : AppCompatActivity() {
    private val requestCode = 0
    private lateinit var firebaseAuth: FirebaseAuth
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firebaseAuth = FirebaseAuth.getInstance()
        firstPhoto.setOnClickListener { requestCamera() }
        newPhoto.setOnClickListener { requestCamera() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        //Get img from camera intent
        val img: Bitmap? = intent?.extras?.get("data") as Bitmap?
        img?.let { img ->
            visibilityItems()
            imageView.setImageBitmap(img)
            this@RegisterActivity.sendBroadcast(intent)
        }

        savePet.setOnClickListener {
            val storage = FirebaseStorage.getInstance("gs://petrescue-app.appspot.com")
            val storageReference = storage.reference
            val petName = petInputLayout.editText?.text.toString()
            val petRefImg = storageReference.child("$petName")
            val baos = ByteArrayOutputStream()
            img!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = petRefImg.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.Image_upload_success),
                    Toast.LENGTH_SHORT
                ).show()
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                petRefImg.downloadUrl.addOnSuccessListener { url ->
                    uploadToDB(url.toString(), petName)
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadToDB(downloadUrl: String, petName: String) {
        val uid = firebaseAuth.uid
        val urlImg = downloadUrl.toString()
        val db = FirebaseDatabase.getInstance().reference
        val dbPath = "pets"
        val newPet =
            PetModel.Pet(
                petName,
                urlImg,
                adoptBy = "",
                rescuedBy = "",
                adoptDate = "",
                rescuedDate = "",
                visitDate = "",
                entryDate = ServerValue.TIMESTAMP,
                deathDate = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli(),
                adopted = false,
                rescued = false,
                requestAdoption = false,
                requestRescue = false,
                NonRequested = false
            )
        if (uid != null) {
            db.child(dbPath).child(petName).setValue(newPet).addOnSuccessListener {
                petRegistered()
            }.addOnFailureListener { e ->
                errorDB(e)
            }
        }
    }

    private fun errorDB(e: Exception) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.error_upload_pet))
            .setMessage(e.message)
            .setPositiveButton(getString(R.string.Ok_value), null)
            .show()
    }

    private fun petRegistered() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Successful_register_pet_title))
            .setMessage(getString(R.string.Successful_register_message))
            .setPositiveButton(getString(R.string.Ok_value)) { _, _ ->
                val nextActivity = Intent(this, MainActivity::class.java)
                startActivity(nextActivity)
            }
            .show()
    }

    private fun visibilityItems() {
        petInputLayout.visibility = View.VISIBLE
        imageView.visibility = View.VISIBLE
        firstPhoto.visibility = View.INVISIBLE
        newPhoto.visibility = View.VISIBLE
        savePet.visibility = View.VISIBLE
    }

    private fun requestCamera() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(callCameraIntent, requestCode)
        }
    }
}
