package arcan.apps.petrescue

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import arcan.apps.petrescue.models.UserModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_newaccount.*
import java.lang.Exception

class NewAccountActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newaccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.newAccountTitle)
        firebaseAuth = FirebaseAuth.getInstance()
        createAccount.setOnClickListener { createAccount() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun createAccount(){
        val db = FirebaseFirestore.getInstance()
        val username = nameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmpasswordInput.text.toString()

        if (password == confirmPassword){
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    task ->
                        if (task.isSuccessful){
                        val newUser = UserModel.User(username, email)
                            db.collection(getString(R.string.userscollection_db))
                                .add(newUser)
                                .addOnSuccessListener{
                                    accountCreatedAlert()
                                }
                                .addOnFailureListener{
                                    e -> errorAccount(e)
                                }
                        }

                }
        }

    }

    private fun accountCreatedAlert() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cuenta creada con éxito!")
            .setMessage("Tu cuenta ha sido creada, presiona el botón ok para regresar a la pantalla anterior.")
            .setPositiveButton(getString(R.string.Ok_value)) { dialog, which -> onBackPressed() }
            .show();
    }

    private fun errorAccount(exception: Exception){
        MaterialAlertDialogBuilder(this)
            .setTitle("Error al crear cuenta")
            .setMessage(exception.message)
            .setPositiveButton(getString(R.string.Ok_value)) { dialog, which -> onBackPressed() }
            .show();
    }
}
