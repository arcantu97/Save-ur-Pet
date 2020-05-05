package arcan.apps.saveurpet.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firestore.v1.StructuredQuery;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import arcan.apps.saveurpet.AdoptarActivity;
import arcan.apps.saveurpet.MainActivity;
import arcan.apps.saveurpet.R;
import arcan.apps.saveurpet.RegisterActivity;
import arcan.apps.saveurpet.RescatarActivity;
import arcan.apps.saveurpet.holders.CardViewHolder;
import arcan.apps.saveurpet.models.Pet;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;


public class IngresadasFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    FloatingActionButton fab;
    ArrayAdapter<CharSequence> adapterCustom;
    Spinner spinner;
    String stateSelectedBySpinner;
    Long adminPermission;
    String name;
    String address;
    String phone1;
    private View rootView;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Pet, CardViewHolder> adapter;
    private FirestoreRecyclerOptions<Pet> options;
    private Query query;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        adminPermission = sharedPref.getLong(getString(R.string.db_permission_user), 0);
        name = sharedPref.getString("username", "none");
        address = sharedPref.getString("address", "none");
        phone1 = sharedPref.getString("Phone1", "none");
    }

    private void openForm() {
        Intent nextActivity = new Intent(getActivity(), RegisterActivity.class);
        nextActivity.putExtra("municipality", stateSelectedBySpinner);
        startActivity(nextActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ingresadas, container, false);
        spinner = rootView.findViewById(R.id.SpinnerState);
        adapterCustom = ArrayAdapter.createFromResource(getContext(), R.array.states_array, android.R.layout.simple_spinner_item);
        adapterCustom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCustom);
        spinner.setOnItemSelectedListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.petRecyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        fab = rootView.findViewById(R.id.floatingActionButton);
        if (adminPermission == 1){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openForm();
                }
            });
        }
        getInitialData();
    }

    private void getInitialData() {
        query = db.collection("pets");
        options = new FirestoreRecyclerOptions.Builder<Pet>()
                .setQuery(query, Pet.class)
                .build();
        attachRecyclerView();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        adapter.stopListening();
        stateSelectedBySpinner = parent.getItemAtPosition(position).toString();
        if (stateSelectedBySpinner.equals("Seleccionar municipio")){
            query = getInstance().collection("pets").whereEqualTo("nonRequested", false).orderBy("deathDate", Query.Direction.DESCENDING);

        }
        else{
            query = getInstance().collection("pets").whereEqualTo("municity", stateSelectedBySpinner).whereEqualTo("nonRequested", false).orderBy("deathDate", Query.Direction.DESCENDING);
        }
        options = new FirestoreRecyclerOptions.Builder<Pet>()
                .setQuery(query, Pet.class)
                .build();
        attachRecyclerView();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void attachRecyclerView() {
        adapter = new FirestoreRecyclerAdapter<Pet, CardViewHolder>(options) {
        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(@NonNull final CardViewHolder holder, int position, @NonNull final Pet model) {
            Picasso.get().load(model.getPetImageURL()).into(holder.petImage);
            holder.petName.setText(model.getPetName());
            if (adminPermission == 1){
                holder.adoptPet.setVisibility(View.INVISIBLE);
                holder.rescuePet.setVisibility(View.INVISIBLE);
            }
            else{
                holder.adoptPet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Adopt(model.getPetName(), model.getPetImageURL());
                    }
                });

                holder.rescuePet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Rescue(model.getPetName(), model.getPetImageURL());
                    }
                });
            }

            long millis = model.getDeathDate() - System.currentTimeMillis();
            new CountDownTimer(millis, 1000){

                @Override
                public void onTick(long millisUntilFinished) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss", Locale.UK);
                    Date date = new Date(millisUntilFinished);
                    String result = formatter.format(date);
                    holder.timer.setText(result);
                }

                @Override
                public void onFinish() {
                    FirebaseFirestore.getInstance().collection("pets")
                            .document(model.getPetName()).update("nonRequested", true);

                    FirebaseFirestore.getInstance().collection(getString(R.string.NonAoR))
                            .document(model.getPetName()).set(model);
                }
            }.start();

        }

    };

        recyclerView.setAdapter(adapter);
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
    private void Adopt(String petName, String petImageURL) {
        Intent intent = new Intent(getActivity(), AdoptarActivity.class);
        intent.putExtra("petName", petName);
        intent.putExtra("petImageURL", petImageURL);
        intent.putExtra("userName", name);
        intent.putExtra("userAddress", address);
        intent.putExtra("userPhone", phone1);
        startActivity(intent);
    }

    private void Rescue(String petName, String petImageURL) {
        Intent intent = new Intent(getActivity(), RescatarActivity.class);
        intent.putExtra("petName", petName);
        intent.putExtra("petImageURL", petImageURL);
        intent.putExtra("userName", name);
        intent.putExtra("userAddress", address);
        intent.putExtra("userPhone", phone1);
        startActivity(intent);
    }



}
