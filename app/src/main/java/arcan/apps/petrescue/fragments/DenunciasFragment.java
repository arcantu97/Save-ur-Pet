package arcan.apps.petrescue.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import arcan.apps.petrescue.ComplaintActivity;
import arcan.apps.petrescue.MainActivity;
import arcan.apps.petrescue.R;
import arcan.apps.petrescue.models.AdoptModel;
import arcan.apps.petrescue.models.ComplaintModel;

public class DenunciasFragment extends Fragment {

    private Long adminPermission;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private String name, address, ph1, ph2, Comments, formattedDate;
    MaterialButton send;
    TextInputLayout namePerson, personAddress, phone1, phone2, comments;
    ListView listView;
    ScrollView scrollView;
    private FirebaseListOptions<ComplaintModel> optionsAdmin;
    private FirebaseListAdapter<ComplaintModel> adapterAdmin;
    Boolean v1,v2,v3,v4;

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
        scrollView = rootView.findViewById(R.id.ScrollView);
        namePerson = rootView.findViewById(R.id.personNameInputLayout);
        personAddress = rootView.findViewById(R.id.addressInputLayout);
        phone1 = rootView.findViewById(R.id.contactPhone1InputLayout);
        phone2 = rootView.findViewById(R.id.contactPhone2InputLayout);
        comments = rootView.findViewById(R.id.comentsInput);
        send = rootView.findViewById(R.id.Send);

        if (adminPermission == 1){
            listView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final Calendar calendar =  Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
        final String date = formatter.format(calendar.getTime());

            send.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    name = namePerson.getEditText().getText().toString().trim();
                    address = personAddress.getEditText().getText().toString().trim();
                    ph1 = phone1.getEditText().getText().toString().trim();
                    ph2 = phone2.getEditText().getText().toString().trim();
                    Comments = comments.getEditText().getText().toString().trim();

                    if (!name.isEmpty() && !address.isEmpty() && !ph1.isEmpty() && !Comments.isEmpty()){
                        Toast.makeText(getContext(), "Tu denuncia ha sido procesada!", Toast.LENGTH_SHORT).show();
                        Date date =  Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
                        formattedDate= formatter.format(date);
                        String key = db.child(getString(R.string.complaints_db)).push().getKey();
                        ComplaintModel complaint = new ComplaintModel(name, ph1, ph2, address, Comments, formattedDate, key);
                        db.child(getString(R.string.complaints_db)).child(key).setValue(complaint);
                    }
                    else{
                        Toast.makeText(getContext(), "Revisa todos los campos", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        final Query query = FirebaseDatabase.getInstance()
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
                TextView Phone = v.findViewById(R.id.userC1);
                TextView Date = v.findViewById(R.id.complaintDate);
                Button details = v.findViewById(R.id.details);

                User.setText(model.getUser());
                Phone.setText(model.getPhone1());
                Date.setText(model.getDate());
                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getDetails(model.getId());
                    }
                });
            }
        };

        adapterAdmin.startListening();
        adapterAdmin.notifyDataSetChanged();
        listView.setAdapter(adapterAdmin);
        return rootView;
    }

    private void getDetails(String id) {
        Intent nextActivity = new Intent(getActivity(), ComplaintActivity.class);
        nextActivity.putExtra("Id", id);
        startActivity(nextActivity);
    }


}
