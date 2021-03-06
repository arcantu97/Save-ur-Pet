package arcan.apps.saveurpet.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import arcan.apps.saveurpet.R;
import arcan.apps.saveurpet.holders.CardViewHolder;
import arcan.apps.saveurpet.holders.RipCardViewHolder;
import arcan.apps.saveurpet.models.AdoptModel;
import arcan.apps.saveurpet.models.Pet;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

public class AdopcionFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private Long adminPermission;
    private String Name;
    private RecyclerView recyclerView;
    private String uid;
    private FirestoreRecyclerAdapter<AdoptModel, RipCardViewHolder> adapterAdmin;
    private FirestoreRecyclerOptions<AdoptModel> optionsAdmin;
    Query query;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        retrievePermission();
        super.onCreate(savedInstanceState);
    }

    private void retrievePermission() {
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        adminPermission = sharedPref.getLong(getString(R.string.db_permission_user), 0);
    }



    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_adopcion, container, false);
        recyclerView = rootView.findViewById(R.id.adoptAdmin);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        if (adminPermission == 1){
            query = getInstance()
                    .collection(getString(R.string.petAdopted_db))
                    .whereEqualTo("adopted", false);
        }
        else{
            query = getInstance()
                    .collection(getString(R.string.petAdopted_db))
                    .whereEqualTo("uidRequest", uid);
        }

        optionsAdmin  = new FirestoreRecyclerOptions.Builder<AdoptModel>()
                .setQuery(query, AdoptModel.class)
                .build();

        adapterAdmin = new FirestoreRecyclerAdapter<AdoptModel, RipCardViewHolder>(optionsAdmin) {
            @NonNull
            @Override
            public RipCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_lv_layout, parent, false);
                return new RipCardViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RipCardViewHolder holder, int position, @NonNull final AdoptModel model) {
                Picasso.get().load(model.getPetImageURL()).into(holder.petImage);
                holder.petName.setText(model.getPetName());
                holder.visitDate.setText(model.getVisitDate());
                holder.requestDate.setText(model.getRequestDate());
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                adminPermission = sharedPref.getLong(getString(R.string.db_permission_user), 0);
                holder.visitDate.setText(String.format("Fecha de visita: %s", model.getVisitDate()));
                holder.requestDate.setText(String.format("Fecha de solicitud: %s", model.getRequestDate()));

                if (adminPermission == 1){
                    holder.approve.setVisibility(View.VISIBLE);
                    holder.reject.setVisibility(View.VISIBLE);
                    holder.requestState.setVisibility(View.GONE);
                    holder.requestDate.setVisibility(View.GONE);
                }

                if (!model.getAdopted()){
                    holder.requestState.setText(getString(R.string.request_state));
                } else {
                    holder.requestState.setText(String.format("Has adoptado a %s", model.getPetName()));
                }

                holder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        approveRequestPet(model.getPersonName(), model.getPetName(), model.getMunicity());
                    }
                });

                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectRequestPet(model, model.getMunicity());
                    }
                });

            }
        };

        adapterAdmin.startListening();
        adapterAdmin.notifyDataSetChanged();
        recyclerView.setAdapter(adapterAdmin);
        return rootView;
    }

    private void rejectRequestPet(final AdoptModel model, final String municity) {
        new MaterialAlertDialogBuilder(getActivity(), R.style.AlertDialogTheme)
                .setTitle("Rechazar solicitud")
                .setMessage("Deseas rechazar la solicitud de " + model.getPersonName() + " para adoptar a " + model.getPetName() + " ?")
                .setPositiveButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date date =  Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
                        final String formattedDate = formatter.format(date);
                        FirebaseFirestore db = getInstance();
                        db.collection(getString(R.string.petAdopted_db)).document(model.getPetName()).delete();
                        db.collection(getString(R.string.petcollection_db)).document(model.getPetName()).update("requestAdoption", false);
                        db.collection(getString(R.string.petcollection_db)).document(model.getPetName()).update("nonRequested", false);
                        db.collection(getString(R.string.petcollection_db)).document(model.getPetName()).update("adopted", false);
                        db.collection(getString(R.string.petcollection_db)).document(model.getPetName()).update("adoptBy", "");
                        db.collection(getString(R.string.petcollection_db)).document(model.getPetName()).update("adoptDate", "");
                        if (municity == "Seleccionar municipio"){
                            Map<String, Object> rejectObject = new HashMap<>();
                            rejectObject.put("municity", "General");
                            rejectObject.put("date", formattedDate);
                            rejectObject.put("type", "adoption/reject");
                            db.collection(getString(R.string.counters)).document().set(rejectObject);
                        } else{
                            Map<String, Object> rejectObject = new HashMap<>();
                            rejectObject.put("municity", municity);
                            rejectObject.put("date", formattedDate);
                            rejectObject.put("type", "adoption/reject");
                            db.collection(getString(R.string.counters)).document().set(rejectObject);
                        }
                    }
                })
                .setNegativeButton("Omitir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void approveRequestPet(final String personName, final String petName, final String municity) {
        Date date =  Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
        final String formattedDate = formatter.format(date);
        new MaterialAlertDialogBuilder(getActivity(), R.style.AlertDialogTheme)
                .setTitle("Aprobar solicitud")
                .setMessage("Deseas aprobar la solicitud de " + personName + " para adoptar a " + petName + " ?")
                .setPositiveButton("Aprobar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore db = getInstance();
                        db.collection(getString(R.string.petcollection_db)).document(petName).update("adopted", true);
                        db.collection(getString(R.string.petcollection_db)).document(petName).update("adoptBy", personName);
                        db.collection(getString(R.string.petcollection_db)).document(petName).update("adoptDate", formattedDate);
                        db.collection(getString(R.string.petAdopted_db)).document(petName).update("adopted", true);
                        db.collection(getString(R.string.petAdopted_db)).document(petName).update("requestAdoption", true);
                        if (municity == "Seleccionar municipio"){
                            Map<String, Object> approveObject = new HashMap<>();
                            approveObject.put("municity", "General");
                            approveObject.put("date", formattedDate);
                            approveObject.put("type", "adoption/approve");
                            db.collection(getString(R.string.counters)).document().set(approveObject);
                        } else{
                            Map<String, Object> approveObject = new HashMap<>();
                            approveObject.put("municity", municity);
                            approveObject.put("date", formattedDate);
                            approveObject.put("type", "adoption/approve");
                            db.collection(getString(R.string.counters)).document().set(approveObject);
                        }

                    }
                })
                .setNegativeButton("Omitir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
        }).show();

    }

    @Override
    public void onStart() {
        super.onStart();
        adapterAdmin.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapterAdmin.stopListening();
    }
}
