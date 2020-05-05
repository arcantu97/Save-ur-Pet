package arcan.apps.saveurpet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import arcan.apps.saveurpet.models.UserModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_newaccount.*
import kotlin.math.log

class NewAccountActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var stateSelected: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newaccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.newAccountTitle)
        firebaseAuth = FirebaseAuth.getInstance()
        createAccountButton.isEnabled = false
        createAccountButton.setOnClickListener { createAccount() }
        loadInstance()
        val spinner: Spinner = findViewById(R.id.stateInputLayout)
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.states_array,
            android.R.layout.simple_spinner_item).also {
                adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
    }

    private fun loadInstance() {

        nameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                usernameInputLayout.error = null
            }
        })

        addressInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                usernameInputLayout.error = null
            }
        })

        phone1Input.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    usernameInputLayout.error = null
                }
            })

        phone2Input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                usernameInputLayout.error = null
            }
        })

        emailInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailInputLayout.error = null
            }

        })

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordInputLayout.error = null
            }

        })

        confirmPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                confirmPasswordInputLayout.error = null
                createAccountButton.isEnabled = true
            }

        })

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        val stateSelected = parent.getItemAtPosition(pos)
        this.stateSelected = stateSelected.toString()
        Log.d("State", this.stateSelected);
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createAccount() {
        val username = usernameInputLayout.editText?.text.toString()
        val email = emailInputLayout.editText?.text.toString()
        val password = passwordInputLayout.editText?.text.toString()
        val confirmPassword = confirmPasswordInputLayout.editText?.text.toString()
        val address = addressInputLayout.editText?.text.toString()
        val phone1 = phone1InputLayout.editText?.text.toString()
        val phone2 = phone2InputLayout.editText?.text.toString()
        if (username.isEmpty()) {
            usernameInputLayout.error = getString(R.string.Error_1)
        }
        if (email.isEmpty()) {
            emailInputLayout.error = getString(R.string.Error_2)
        }
        if (password.isEmpty()) {
            passwordInputLayout.error = getString(R.string.Error_3)
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.error = getString(R.string.Error_4)
        }
        if (address.isEmpty()) {
            confirmPasswordInputLayout.error = getString(R.string.Error_4)
        }
        if (phone1.isEmpty()) {
            confirmPasswordInputLayout.error = getString(R.string.Error_5)
        }
        if(this.stateSelected.isEmpty()){
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.Error_missedFields_title))
                .setMessage(getString(R.string.Error_6))
                .setPositiveButton(getString(R.string.Ok_value), null)
                .show()
        }

        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && address.isNotEmpty() && phone1.isNotEmpty() && this.stateSelected
                .isNotEmpty()){
            if (password.equals(confirmPassword)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val db = FirebaseDatabase.getInstance().reference
                            val uid = task.result?.user?.uid.toString()
                            val newUser = UserModel.User(0, username, email, uid, address, this.stateSelected, phone1, phone2)
                            val dbPath = getString(R.string.userscollection_db)
                            db.child(dbPath)
                                .child(uid)
                                .setValue(newUser)
                                .addOnSuccessListener {
                                    accountCreatedAlert()
                                }
                                .addOnFailureListener { e ->
                                    errorAccountDB(e)
                                }

                        }
                        else{
                            errorAccount()
                        }
                    }
            } else {
                errorPassword()
            }
        }
        else{
            errorFields()
        }

    }


    private fun errorFields() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Error_missedFields_title))
            .setMessage(getString(R.string.Error_missedFields_message))
            .setPositiveButton(getString(R.string.Ok_value), null)
            .show()
    }

    private fun isEmpty(editText: EditText): Boolean {
        return editText.text.toString().trim().isEmpty();
    }

    private fun errorPassword() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Password_error_Title))
            .setMessage(getString(R.string.Password_error_message))
            .setPositiveButton(getString(R.string.Ok_value), null)
            .show()
    }

    private fun accountCreatedAlert() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Successful_account_Title))
            .setMessage(getString(R.string.Successfull_account_Message))
            .setPositiveButton(getString(R.string.Ok_value)) { _, _ ->
                val nextActivity = Intent(this, LoginActivity::class.java)
                startActivity(nextActivity)
            }
            .show()
    }

    private fun errorAccountDB(exception: Exception) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Error_Acount_TItle))
            .setMessage(exception.message)
            .setPositiveButton(getString(R.string.Ok_value), null)
            .show()
    }
    private fun errorAccount() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.Error_Acount_TItle))
            .setMessage(getString(R.string.Error_account_used))
            .setPositiveButton(getString(R.string.Ok_value), null)
            .show()
    }
}
