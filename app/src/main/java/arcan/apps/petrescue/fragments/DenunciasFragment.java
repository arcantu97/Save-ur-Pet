package arcan.apps.petrescue.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import arcan.apps.petrescue.R;
import arcan.apps.petrescue.models.AdoptModel;
import arcan.apps.petrescue.models.ComplaintModel;

public class DenunciasFragment extends Fragment {

    private Long adminPermission;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private String name;
    private TextView comments, header;
    MaterialButton send;
    ListView listView;
    private FirebaseListOptions<ComplaintModel> optionsAdmin;
    private FirebaseListAdapter<ComplaintModel> adapterAdmin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrievePermission();
    }


    private void retrievePermission() {
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        adminPermission = sharedPref.getLong(getString(R.string.db_permission_user), 0);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.userscollection_db))
                .child(uid)
                .child("username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_denuncias, container, false);
        listView = rootView.findViewById(R.id.denuncias);
        comments = rootView.findViewById(R.id.denunciaText);
        send = rootView.findViewById(R.id.enviarDenuncia);
        header = rootView.findViewById(R.id.titleDenuncia);

        if (adminPermission == 1){
            listView.setVisibility(View.VISIBLE);
            comments.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
        }

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final Calendar calendar =  Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
        final String date = formatter.format(calendar.getTime());

        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               String text = comments.getText().toString();
                ComplaintModel complaint = new ComplaintModel(text, date, name);
                db.child(getString(R.string.complaints_db)).push().setValue(complaint);
            }
        });

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.complaints_db))
                .orderByChild("date");

        optionsAdmin = new FirebaseListOptions.Builder<ComplaintModel>()
                .setLayout(R.layout.complaint_card)
                .setLifecycleOwner(DenunciasFragment.this)
                .setQuery(query, ComplaintModel.class).build();

        adapterAdmin = new FirebaseListAdapter<ComplaintModel>(optionsAdmin) {
            @Override
            protected void populateView(@NonNull View v, @NonNull final ComplaintModel model, int position) {
                TextView User = v.findViewById(R.id.userName);
                TextView Date = v.findViewById(R.id.complaintDate);
                Button details = v.findViewById(R.id.details);

                User.setText(model.getUser());
                Date.setText(model.getDate());
                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDetails(model.getComment());
                    }
                });
            }
        };

        adapterAdmin.startListening();
        adapterAdmin.notifyDataSetChanged();
        listView.setAdapter(adapterAdmin);
        return rootView;
    }

    private void getDetails(String comment) {
        new MaterialAlertDialogBuilder(getActivity(), R.style.AlertDialogTheme)
                .setTitle("Detalles de denuncia")
                .setMessage(comment)
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    }
                }).show();

    }
}
