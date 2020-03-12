package arcan.apps.petrescue

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.core.view.get
import arcan.apps.petrescue.fragments.DenunciasFragment
import arcan.apps.petrescue.fragments.IngresadosFragment
import arcan.apps.petrescue.fragments.RescatadosFragment
import arcan.apps.petrescue.fragments.RipFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigation: BottomNavigationView = bottom_navigation


        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.adopcion -> {
                    supportActionBar?.title = getString(R.string.adopcion_Title)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, IngresadosFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.rescate -> {
                    supportActionBar?.title = getString(R.string.rescate_Title)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, RescatadosFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.denuncias -> {
                    supportActionBar?.title = getString(R.string.denuncias_Title)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, DenunciasFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.rip -> {
                    supportActionBar?.title = getString(R.string.rip_Title)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, RipFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }

            }


            false
        }

        bottomNavigation.selectedItemId = R.id.adopcion
    }
}


