package arcan.apps.petrescue

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import arcan.apps.petrescue.models.ManagePermissions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.security.Permissions


class LoginActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()
        requestPermissions()
    }



    private fun requestPermissions() {
        val permissionList = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )

        managePermissions = ManagePermissions(this, permissionList, PermissionsRequestCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            managePermissions.checkPermissions()
        }
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
        val email = emailInputLayout.editText?.text.toString()
        val password = passwordInputLayout.editText?.text.toString()

        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                        task ->
                    if (task.isSuccessful){
                        val nextActivity = Intent(this, MainActivity::class.java)
                        startActivity(nextActivity)
                    }
                    else {
                        MaterialAlertDialogBuilder(this)
                            .setTitle(getString(R.string.Error_title))
                            .setMessage(task.exception?.message)
                            .setPositiveButton(getString(R.string.Ok_value), null)
                            .show();
                    }
                }
        }
        else{
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.Error_title))
                .setMessage(getString(R.string.Error_empty))
                .setPositiveButton(getString(R.string.Ok_value), null)
                .show();
        }

    }

    private fun registerUser(){
        val nextActivity = Intent(this, NewAccountActivity::class.java)
        startActivity(nextActivity)
    }
}
