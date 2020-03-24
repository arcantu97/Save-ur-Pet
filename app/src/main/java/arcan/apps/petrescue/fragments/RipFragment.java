package arcan.apps.petrescue.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import arcan.apps.petrescue.R;
import arcan.apps.petrescue.models.AdoptModel;
import arcan.apps.petrescue.models.RipModel;

public class RipFragment extends Fragment {

    private FirebaseListOptions<RipModel> options;
    private FirebaseListAdapter<RipModel> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rip, container, false);
        ListView listView = rootView.findViewById(R.id.listView);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.NonAoR))
                .orderByChild("deathDate");
        options = new FirebaseListOptions.Builder<RipModel>()
                .setLayout(R.layout.card_lv_layout)
                .setLifecycleOwner(RipFragment.this)
                .setQuery(query, RipModel.class).build();

        adapter = new FirebaseListAdapter<RipModel>(options) {
        @Override
        protected void populateView(@NonNull View v, @NonNull final RipModel model, int position) {
            ImageView petImage = v.findViewById(R.id.petImageAdopt);
            TextView petName = v.findViewById(R.id.petNameAdopt);
            TextView requestDate = v.findViewById(R.id.requestDate);
            TextView visit = v.findViewById(R.id.visitDate);
            TextView rd = v.findViewById(R.id.requestState);

            visit.setVisibility(View.GONE);
            rd.setVisibility(View.GONE);

            Picasso.get().load(model.getPetImageURL()).into(petImage);
            petName.setText(model.getPetName());

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
            final String date = formatter.format(model.getDeathDate());
            requestDate.setText(String.format("Inabilitado el: %s", date));
        }

        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        return rootView;
    }
}
