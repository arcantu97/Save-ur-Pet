package arcan.apps.petrescue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()
        registerUsers()
    }

    override fun onStart() {
        super.onStart()
        var currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val nextActivity = Intent(this, MainActivity::class.java)
            startActivity(nextActivity)
        }
    }

    private fun registerUsers(){
        loginButton.setOnClickListener {
            val email = emailInputLayout.editText.toString()
            val password = passwordInputLayout.editText.toString()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        task ->
                            if (task.isSuccessful){
                                val nextActivity = Intent(this, MainActivity::class.java)
                                startActivity(nextActivity)
                            }
                            else {
                                passwordInputLayout.error = task.exception?.message
                            }
                    }
        }
    }
}
