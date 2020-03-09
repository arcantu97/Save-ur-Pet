package arcan.apps.petrescue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        var currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val nextActivity = Intent(this, MainActivity::class.java)
            startActivity(nextActivity)
        }
        else {
            loginButton.setOnClickListener { loginUser() }
            createAccount.setOnClickListener { registerUser() }
        }
    }

    private fun loginUser(){
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        task ->
                            if (task.isSuccessful){
                                val nextActivity = Intent(this, MainActivity::class.java)
                                startActivity(nextActivity)
                            }
                            else {
                                MaterialAlertDialogBuilder(this)
                                    .setTitle("Error")
                                    .setMessage(task.exception?.message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                            }
        }
    }

    private fun registerUser(){
        val nextActivity = Intent(this, NewAccountActivity::class.java)
        startActivity(nextActivity)
    }
}
