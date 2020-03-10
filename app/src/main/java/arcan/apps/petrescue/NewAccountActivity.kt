package arcan.apps.petrescue

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import arcan.apps.petrescue.models.UserModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_newaccount.*

class NewAccountActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newaccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.newAccountTitle)
        firebaseAuth = FirebaseAuth.getInstance()
        createAccountButton.isEnabled = false
        createAccountButton.setOnClickListener { createAccount() }
        loadInstances()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createAccount() {
        val db = FirebaseFirestore.getInstance()
        val username = nameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        if (password.equals(confirmPassword)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val newUser = UserModel.User(username, email)
                        val dbPath = getString(R.string.userscollection_db)
                        db.collection(dbPath)
                            .add(newUser)
                            .addOnSuccessListener {
                                accountCreatedAlert()
                            }
                            .addOnFailureListener { e ->
                                errorAccount(e)
                            }
                    }
                }
        } else {
            errorPassword()
        }
    }

    private fun errorPassword() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Password_error_Title))
            .setMessage(getString(R.string.Password_error_message))
            .setPositiveButton(getString(R.string.Ok_value), null)
            .show()
    }

    private fun loadInstances() {
        confirmPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val conf = s.toString()
                if (conf.isNotEmpty()) {
                    createAccountButton.isEnabled = true
                }
            }
        })
    }

    private fun accountCreatedAlert() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Successful_account_Title))
            .setMessage(getString(R.string.Successfull_account_Message))
            .setPositiveButton(getString(R.string.Ok_value)) {
                _, _ -> onBackPressed()
            }
            .show()
    }

    private fun errorAccount(exception: Exception) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Error_Acount_TItle))
            .setMessage(exception.message)
            .setPositiveButton(getString(R.string.Ok_value), null)
            .show()
    }
}
