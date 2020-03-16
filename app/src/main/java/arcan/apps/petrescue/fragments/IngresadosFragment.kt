package arcan.apps.petrescue.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import arcan.apps.petrescue.MainActivity

import arcan.apps.petrescue.R
import arcan.apps.petrescue.RegisterActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.model.SnapshotVersion
import kotlinx.android.synthetic.main.fragment_ingresados.*

/**
 * A simple [Fragment] subclass.
 */
class IngresadosFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var uid: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ingresados, container, false)
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth = FirebaseAuth.getInstance()
        uid = firebaseAuth.uid.toString()
        val db = FirebaseFirestore.getInstance()
        db.collection(getString(R.string.userscollection_db)).document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val adminPermission: String = doc.data?.get("adminPermission").toString()

                if (adminPermission == getString(R.string.ADMIN_Permission)){
                    val fab: FloatingActionButton = floatingActionButton
                    fab.visibility = View.VISIBLE
                    fab.setOnClickListener {
                        openForm()
                    }
                }
            }
    }

    private fun openForm() {
        val nextActivity = Intent(activity, RegisterActivity::class.java)
        activity?.startActivity(nextActivity)
    }

}
