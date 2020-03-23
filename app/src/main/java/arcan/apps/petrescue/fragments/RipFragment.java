package arcan.apps.petrescue.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arcan.apps.petrescue.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RipFragment extends Fragment {

    public RipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rip, container, false);
    }
}
