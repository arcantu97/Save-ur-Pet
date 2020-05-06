package arcan.apps.saveurpet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import arcan.apps.saveurpet.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity() : AppCompatActivity() {

    var firebaseAuth: FirebaseAuth? = null
    private var adminPermission: Long? = null

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        retrievePermission()
        retrieveName()
        retrieveAddress()
        retrievePhone1()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = getActivity(this)!!.getPreferences(Context.MODE_PRIVATE)

        logoutBtn.setOnClickListener {
            logout()
        }
        val bottomNavigation: BottomNavigationView = bottom_navigation
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    Title.text = ""
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, IngresadasFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.adopcion -> {
                    Title.text = "Adopción"
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, AdopcionFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.rescate -> {
                    Title.text = "Rescate"
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, RescatadasFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.denuncias -> {
                    Title.text = "Denuncias"
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, DenunciasFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.rip -> {
                    Title.text = "RIP"
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, RipFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }

        bottomNavigation.selectedItemId = R.id.home
    }

    private fun retrieveName() {
        firebaseAuth = FirebaseAuth.getInstance();
        var uid = firebaseAuth?.uid
        val dbPath = getString(R.string.userscollection_db)
        val db = FirebaseDatabase.getInstance().reference
        uid?.let {
            db.child(dbPath)
                .child(it)
                .child("username")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE) ?: return
                        with(sharedPref.edit()) {
                            p0.getValue(String::class.java)?.let { it ->
                                putString("username", it)
                            }
                            apply()
                        }
                    }
                })
        }
    }

    private fun retrieveAddress() {
        firebaseAuth = FirebaseAuth.getInstance();
        var uid = firebaseAuth?.uid
        val dbPath = getString(R.string.userscollection_db)
        val db = FirebaseDatabase.getInstance().reference
        uid?.let {
            db.child(dbPath)
                .child(it)
                .child("address")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE) ?: return
                        with(sharedPref.edit()) {
                            p0.getValue(String::class.java)?.let { it ->
                                putString("address", it)
                            }
                            apply()
                        }
                    }
                })
        }
    }

    private fun retrievePhone1() {
        firebaseAuth = FirebaseAuth.getInstance();
        var uid = firebaseAuth?.uid
        val dbPath = getString(R.string.userscollection_db)
        val db = FirebaseDatabase.getInstance().reference
        uid?.let {
            db.child(dbPath)
                .child(it)
                .child("phone1")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE) ?: return
                        with(sharedPref.edit()) {
                            p0.getValue(String::class.java)?.let { it ->
                                putString("Phone1", it)
                            }
                            apply()
                        }
                    }
                })
        }
    }

    private fun retrievePermission() {
        firebaseAuth = FirebaseAuth.getInstance();
        var uid = firebaseAuth?.uid
        val dbPath = getString(R.string.userscollection_db)
        val db = FirebaseDatabase.getInstance().reference
        uid?.let {
            db.child(dbPath)
                .child(it)
                .child("adminPermission")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE) ?: return
                        with(sharedPref.edit()) {
                            p0.getValue(Long::class.java)?.let { it ->
                                putLong(getString(R.string.db_permission_user), it)
                            }
                            apply()
                        }
                    }
                })
        }
    }


    fun logout() {
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(getString(R.string.logout_title_dialog))
            .setMessage(getString(R.string.logout_message))
            .setPositiveButton(getString(R.string.acept_btn_title)) { dialog, which ->
                firebaseAuth?.signOut()
                val nextActivity = Intent(this, LoginActivity::class.java)
                nextActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
                nextActivity.flags = Intent.FLAG_ACTIVITY_NO_HISTORY;
                startActivity(nextActivity)
            }
            .setNegativeButton(
                getString(R.string.cancel_btn_title)
            ) { dialog, which -> dialog.dismiss() }.show()
    }


}


