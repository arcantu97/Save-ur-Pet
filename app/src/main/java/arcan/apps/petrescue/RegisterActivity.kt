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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.type.Date
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class RegisterActivity : AppCompatActivity() {
    private val requestCode = 0
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firebaseAuth = FirebaseAuth.getInstance()
        firstPhoto.setOnClickListener { requestCamera() }
        newPhoto.setOnClickListener {requestCamera() }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        //Get img from camera intent
        val img: Bitmap? = intent?.extras?.get("data") as Bitmap?
        val petName = petInputLayout.editText?.text.toString()
        img?.let { img ->
           visibilityItems()
            imageView.setImageBitmap(img)
            this@RegisterActivity.sendBroadcast(intent)
        }

        savePet.setOnClickListener {
            val storage = FirebaseStorage.getInstance("gs://petrescue-app.appspot.com")
            val storageReference = storage.reference
            val petRefImg = storageReference.child("$petName")
            val baos = ByteArrayOutputStream()
            img!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = petRefImg.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(applicationContext, getString(R.string.Image_upload_success), Toast.LENGTH_SHORT).show()
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                task ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                petRefImg.downloadUrl.addOnSuccessListener {
                    url -> uploadToDB(url.toString(), petName)
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadToDB(downloadUrl: String, petName: String) {
        val uid = firebaseAuth.uid
        val urlImg = downloadUrl.toString()
        val db = FirebaseFirestore.getInstance()
        val dbPath = getString(R.string.petcollection_db)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)

        val newPet = PetModel.Pet(petName, urlImg, Adopted = false, Rescued = false,
            entryDate = formatted, deathDate = "")
        uid?.let {
            db.collection(dbPath)
                .document(it)
                .set(newPet)
                .addOnSuccessListener {
                    petRegistered()
                }
                .addOnFailureListener { e ->
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
        imageView.visibility = View.VISIBLE
        firstPhoto.visibility = View.INVISIBLE
        newPhoto.visibility = View.VISIBLE
        savePet.visibility = View.VISIBLE
    }

    private fun requestCamera() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(callCameraIntent.resolveActivity(packageManager)!=null){
            startActivityForResult(callCameraIntent, requestCode)
        }
    }
}
