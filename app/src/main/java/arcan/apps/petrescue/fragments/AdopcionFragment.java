package arcan.apps.petrescue.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import arcan.apps.petrescue.MainActivity;
import arcan.apps.petrescue.R;
import arcan.apps.petrescue.models.AdoptModel;
import arcan.apps.petrescue.models.Pet;

public class AdopcionFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private Long adminPermission;
    private ListView ListViewAdmin;
    private String uid;
    private FirebaseListOptions<AdoptModel> optionsAdmin;
    private FirebaseListAdapter<AdoptModel> adapterAdmin;
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
        ListViewAdmin = rootView.findViewById(R.id.adoptAdmin);
        if (adminPermission == 1){
            query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.petAdopted_db))
                    .orderByChild("requestAdoption")
                    .equalTo(true);
        }
        else{
            query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.petAdopted_db))
                    .orderByChild("uidRequest")
                    .equalTo(uid);
        }

        optionsAdmin = new FirebaseListOptions.Builder<AdoptModel>()
                .setLayout(R.layout.card_lv_layout)
                .setLifecycleOwner(AdopcionFragment.this)
                .setQuery(query, AdoptModel.class).build();
        adapterAdmin = new FirebaseListAdapter<AdoptModel>(optionsAdmin) {
            @Override
            protected void populateView(@NonNull View v, @NonNull final AdoptModel model, int position) {
                ImageView petImage = v.findViewById(R.id.petImageAdopt);
                TextView petName = v.findViewById(R.id.petNameAdopt);
                TextView visitDate = v.findViewById(R.id.visitDate);
                TextView requestDate = v.findViewById(R.id.requestDate);
                TextView requestState = v.findViewById(R.id.requestState);
                MaterialButton Approve = v.findViewById(R.id.approve);
                MaterialButton Reject = v.findViewById(R.id.reject);
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                adminPermission = sharedPref.getLong(getString(R.string.db_permission_user), 0);

                Picasso.get().load(model.getPetImageURL()).into(petImage);
                petName.setText(model.getPetName());
                visitDate.setText(String.format("Fecha de visita: %s", model.getVisitDate()));
                requestDate.setText(String.format("Fecha de solicitud: %s", model.getRequestDate()));

                if (adminPermission == 1){
                    Approve.setVisibility(View.VISIBLE);
                    Reject.setVisibility(View.VISIBLE);
                    requestState.setVisibility(View.GONE);
                    requestDate.setVisibility(View.GONE);
                }

                if (!model.getAdopted()){
                    requestState.setText(R.string.request_state);
                }
                else{
                    requestState.setText(String.format("Has adoptado a %s", model.getPetName()));
                }

                Approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        approveRequestPet(model.getPersonName(), model.getPetName());
                    }
                });

                Reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectRequestPet(model);
                    }
                });
            }
        };

        adapterAdmin.startListening();
        adapterAdmin.notifyDataSetChanged();
        ListViewAdmin.setAdapter(adapterAdmin);
        return rootView;
    }

    private void rejectRequestPet(final AdoptModel model) {
        new MaterialAlertDialogBuilder(getActivity(), R.style.AlertDialogTheme)
                .setTitle("Rechazar solicitud")
                .setMessage("Deseas rechazar la solicitud de " + model.getPersonName() + " para adoptar a " + model.getPetName() + " ?")
                .setPositiveButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        deleteNode(model.getPetName());
                        db.child(getString(R.string.petAdopted_db)).child(model.getPetName()).removeValue();

                    }
                })
                .setNegativeButton("Omitir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void approveRequestPet(final String personName, final String petName) {
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
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(getString(R.string.petcollection_db)).child(petName).child("adopted").setValue(true);
                        db.child(getString(R.string.petcollection_db)).child(petName).child("adoptBy").setValue(personName);
                        db.child(getString(R.string.petcollection_db)).child(petName).child("adoptDate").setValue(formattedDate);
                        db.child(getString(R.string.petAdopted_db)).child(petName).child("adopted").setValue(true);
                        db.child(getString(R.string.petAdopted_db)).child(petName).child("requestAdoption").setValue(false);
                    }
                })
                .setNegativeButton("Omitir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
        }).show();

    }

    private void deleteNode(final String petName) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(getString(R.string.petcollection_db)).child(petName);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                Pet pet = dataSnapshot.getValue(Pet.class);
                db.child(getString(R.string.NonAoR)).child(petName).setValue(pet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterAdmin.startListening();
    }
}
