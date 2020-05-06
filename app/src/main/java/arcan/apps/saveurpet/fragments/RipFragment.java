package arcan.apps.saveurpet.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import arcan.apps.saveurpet.R;
import arcan.apps.saveurpet.holders.CardViewHolder;
import arcan.apps.saveurpet.holders.RipCardViewHolder;
import arcan.apps.saveurpet.models.Pet;
import arcan.apps.saveurpet.models.RipModel;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

public class RipFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    View rootView;
    Spinner spinner;
    RecyclerView recyclerView;
    String stateSelectedBySpinner;
    ArrayAdapter<CharSequence> adapterCustom;
    private FirestoreRecyclerOptions<RipModel> options;
    private FirestoreRecyclerAdapter<RipModel, RipCardViewHolder> adapter;
    private FirebaseFirestore db;
    private Query query;
    ImageView upSort, downSort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_rip, container, false);
        spinner = rootView.findViewById(R.id.spinnerMun);
        adapterCustom = ArrayAdapter.createFromResource(getContext(), R.array.states_array, android.R.layout.simple_spinner_item);
        adapterCustom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCustom);
        spinner.setOnItemSelectedListener(this);
        upSort = rootView.findViewById(R.id.sortUp);
        downSort = rootView.findViewById(R.id.sortDown);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.recyclerViewRIP);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        getInitialData();
    }

    private void getInitialData() {
        query = db.collection(getString(R.string.NonAoR));
        options = new FirestoreRecyclerOptions.Builder<RipModel>()
                .setQuery(query, RipModel.class)
                .build();
        attachRecyclerView();
        adapter.notifyDataSetChanged();
    }

    private void attachRecyclerView() {
        adapter = new FirestoreRecyclerAdapter<RipModel, RipCardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RipCardViewHolder holder, int position, @NonNull RipModel model) {
                Picasso.get().load(model.getPetImageURL()).into(holder.petImage);
                holder.petName.setText(model.getPetName());
                holder.requestState.setVisibility(View.GONE);
                holder.visitDate.setVisibility(View.GONE);
                holder.approve.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
                final String date = formatter.format(model.getDeathDate());
                holder.requestDate.setText(date);
            }

            @NonNull
            @Override
            public RipCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_lv_layout, parent, false);
                return new RipCardViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        adapter.stopListening();
        stateSelectedBySpinner = parent.getItemAtPosition(position).toString();
        if (stateSelectedBySpinner.equals("Seleccionar municipio")){
            query = getInstance().collection("NonAdoptedORRescued").whereEqualTo("nonRequested", true).orderBy("deathDate", Query.Direction.DESCENDING);
        }
        else{
            query = getInstance().collection("NonAdoptedORRescued").whereEqualTo("nonRequested", true).whereEqualTo("municity", stateSelectedBySpinner).orderBy("deathDate", Query.Direction.DESCENDING);
        }
        options = new FirestoreRecyclerOptions.Builder<RipModel>()
                .setQuery(query, RipModel.class)
                .build();
        attachRecyclerView();
        adapter.startListening();
        adapter.notifyDataSetChanged();

        downSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.stopListening();
                if (stateSelectedBySpinner.equals("Seleccionar municipio")){
                    query = getInstance().collection("NonAdoptedORRescued").whereEqualTo("nonRequested", true).orderBy("deathDate", Query.Direction.ASCENDING);
                }
                else{
                    query = getInstance().collection("NonAdoptedORRescued").whereEqualTo("nonRequested", true).whereEqualTo("municity", stateSelectedBySpinner).orderBy("deathDate", Query.Direction.ASCENDING);
                }
                options = new FirestoreRecyclerOptions.Builder<RipModel>()
                        .setQuery(query, RipModel.class)
                        .build();
                attachRecyclerView();
                adapter.startListening();
                adapter.notifyDataSetChanged();
            }
        });

        upSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.stopListening();
                if (stateSelectedBySpinner.equals("Seleccionar municipio")){
                    query = getInstance().collection("NonAdoptedORRescued").whereEqualTo("nonRequested", true).orderBy("deathDate", Query.Direction.DESCENDING);
                }
                else{
                    query = getInstance().collection("NonAdoptedORRescued").whereEqualTo("nonRequested", true).whereEqualTo("municity", stateSelectedBySpinner).orderBy("deathDate", Query.Direction.DESCENDING);
                }
                options = new FirestoreRecyclerOptions.Builder<RipModel>()
                        .setQuery(query, RipModel.class)
                        .build();
                attachRecyclerView();
                adapter.startListening();
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
