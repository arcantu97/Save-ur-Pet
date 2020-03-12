package arcan.apps.petrescue.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import arcan.apps.petrescue.R

/**
 * A simple [Fragment] subclass.
 */
class IngresadosFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_ingresados, container, false)

    companion object {
        fun newInstance(): IngresadosFragment = IngresadosFragment()
    }

}
