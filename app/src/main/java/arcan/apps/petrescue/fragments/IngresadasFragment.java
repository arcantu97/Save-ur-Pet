package arcan.apps.petrescue.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import arcan.apps.petrescue.AdoptarActivity;
import arcan.apps.petrescue.MainActivity;
import arcan.apps.petrescue.R;
import arcan.apps.petrescue.RegisterActivity;
import arcan.apps.petrescue.RescatarActivity;
import arcan.apps.petrescue.holders.CardViewHolder;
import arcan.apps.petrescue.models.Pet;


public class IngresadasFragment extends Fragment {
    FloatingActionButton fab;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String uid;
    Long adminPermission;
    private View rootView;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<Pet> options;
    FirebaseRecyclerAdapter<Pet, CardViewHolder> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        adminPermission = sharedPref.getLong(getString(R.string.db_permission_user), 0);
    }

    private void openForm() {
        Intent nextActivity = new Intent(getActivity(), RegisterActivity.class);
        startActivity(nextActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ingresadas, container, false);
        recyclerView = rootView.findViewById(R.id.petRecyclerView);
        recyclerView.setHasFixedSize(true);
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

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.petcollection_db)).
                        orderByChild("nonRequested").
                        equalTo(false);
        options = new FirebaseRecyclerOptions.Builder<Pet>().setQuery(query, Pet.class).build();

        adapter = new FirebaseRecyclerAdapter<Pet, CardViewHolder>(options) {
            @NonNull
            @Override
            public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
                return new CardViewHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull final CardViewHolder holder, final int position, @NonNull final Pet model) {
                Picasso.get().load(model.getPetImageURL()).into(holder.petImage);
                holder.petName.setText(model.getPetName());
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

                long millis = model.getDeathDate() - System.currentTimeMillis();

                new CountDownTimer(millis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        @SuppressLint("DefaultLocale")
                        SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss", Locale.UK);
                        Date date = new Date(millisUntilFinished);
                        String result = formatter.format(date);
                        holder.timer.setText(result);

                    }

                    @Override
                    public void onFinish() {
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(getString(R.string.petcollection_db)).child(model.getPetName()).child("nonRequested").setValue(true);
                        db.child(getString(R.string.NonAoR)).child(model.getPetName()).setValue(model);
                    }
                }.start();

            }

        };


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        return rootView;

    }


    private void Adopt(String petName, String petImageURL) {
        Intent intent = new Intent(getActivity(), AdoptarActivity.class);
        intent.putExtra("petName", petName);
        intent.putExtra("petImageURL", petImageURL);
        startActivity(intent);
    }

    private void Rescue(String petName, String petImageURL) {
        Intent intent = new Intent(getActivity(), RescatarActivity.class);
        intent.putExtra("petName", petName);
        intent.putExtra("petImageURL", petImageURL);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
