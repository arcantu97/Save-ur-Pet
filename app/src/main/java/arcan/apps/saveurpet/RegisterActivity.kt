package arcan.apps.saveurpet

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import arcan.apps.saveurpet.models.PetModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.card_layout.*
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.temporal.ChronoUnit


class RegisterActivity : AppCompatActivity() {
    private val requestCode = 0
    private val PICK_CODE = 1000;
    private lateinit var firebaseAuth: FirebaseAuth
    private var selectedMunicity: String? = null
    private lateinit var img: Any
    private lateinit var petName: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firebaseAuth = FirebaseAuth.getInstance()
        firstPhoto.setOnClickListener { requestCamera() }
        galleryPhoto.setOnClickListener { requestImage() }
        selectedMunicity = intent.getStringExtra("municipality");
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (intent?.hasExtra("data")!!){
           img = intent.extras?.get("data") as Bitmap
            img.let { img ->
                imageView.setImageBitmap(img as Bitmap?)
                this@RegisterActivity.sendBroadcast(intent)
            }
        }
        else{
            val intentRes = intent.data
            img = MediaStore.Images.Media.getBitmap(this.contentResolver, intentRes)
            imageView.setImageBitmap(img as Bitmap?)
        }

        savePet.setOnClickListener {
            val storage = FirebaseStorage.getInstance("gs://petrescue-app.appspot.com")
            val storageReference = storage.reference
            petName = if (petInputLayout.editText?.text.toString().isEmpty()){
                Instant.now().toEpochMilli().toString()
            } else{
                petInputLayout.editText?.text.toString()
            }
            val petRefImg = storageReference.child(petName)
            val baos = ByteArrayOutputStream()
            val imgData: Bitmap? = img as Bitmap?
            imgData!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = petRefImg.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.Image_upload_error),
                    Toast.LENGTH_SHORT
                ).show()

            }.addOnSuccessListener {
                petRefImg.downloadUrl.addOnSuccessListener { url ->
                    uploadToDB(url.toString(), petName)
                }

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadToDB(downloadUrl: Any, petName: String) {
        val uid = firebaseAuth.uid
        val urlImg = downloadUrl.toString()
        val db = FirebaseFirestore.getInstance()
        val newPet =
            PetModel.Pet(
                petName,
                urlImg,
                adoptBy = "",
                rescuedBy = "",
                adoptDate = "",
                rescuedDate = "",
                visitDate = "",
                entryDate = Instant.now().toEpochMilli(),
                deathDate = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli(),
                adopted = false,
                rescued = false,
                requestAdoption = false,
                requestRescue = false,
                NonRequested = false,
                municity = this.selectedMunicity
            )
        if (uid != null) {
            db.collection("pets").document(petName).set(newPet).addOnSuccessListener {
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

    private fun requestImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_CODE)

    }
    private fun requestCamera() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(callCameraIntent, requestCode)
        }
    }
}
