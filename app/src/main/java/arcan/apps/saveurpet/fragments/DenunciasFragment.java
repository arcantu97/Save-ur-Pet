package arcan.apps.saveurpet.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import arcan.apps.saveurpet.ComplaintActivity;
import arcan.apps.saveurpet.R;
import arcan.apps.saveurpet.models.ComplaintModel;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

public class DenunciasFragment extends Fragment implements AdapterView.OnItemSelectedListener  {
    private int PICK_CODE = 1000;
    String uN, uA, uP;
    String stateSelectedBySpinner;
    private Long adminPermission;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private String name, address, ph1, ph2, Comments, formattedDate;
    MaterialButton send, imagePick;
    TextInputLayout namePerson, personAddress, phone1, phone2, comments;
    ImageView imageView;
    ListView listView;
    ScrollView scrollView;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapterCustom;
    private FirebaseListOptions<ComplaintModel> optionsAdmin;
    private FirebaseListAdapter<ComplaintModel> adapterAdmin;
    Uri intentRes;


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
        uN = sharedPref.getString("username", "none");
        uA = sharedPref.getString("address", "none");
        uP = sharedPref.getString("Phone1", "none");
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
        imageView = rootView.findViewById(R.id.imageView);
        imagePick = rootView.findViewById(R.id.getImageGallery);
        namePerson = rootView.findViewById(R.id.personNameInputLayout);
        personAddress = rootView.findViewById(R.id.addressInputLayout);
        phone1 = rootView.findViewById(R.id.contactPhone1InputLayout);
        phone2 = rootView.findViewById(R.id.contactPhone2InputLayout);
        comments = rootView.findViewById(R.id.comentsInput);
        send = rootView.findViewById(R.id.Send);
        spinner = rootView.findViewById(R.id.SpinnerState);
        adapterCustom = ArrayAdapter.createFromResource(getContext(), R.array.states_array, android.R.layout.simple_spinner_item);
        adapterCustom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCustom);
        spinner.setOnItemSelectedListener(this);
        if (adminPermission == 1){
            listView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        namePerson.getEditText().setText(uN);
        personAddress.getEditText().setText(uA);
        phone1.getEditText().setText(uP);
            imagePick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestImage();
                }
            });
            send.setOnClickListener(new View.OnClickListener(){

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    uploadComplaint();
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
                final Button details = v.findViewById(R.id.details);
                final Button accept = v.findViewById(R.id.accept);
                final Button deny = v.findViewById(R.id.deny);
                User.setText(model.getUser());
                Phone.setText(model.getPhone1());
                Date.setText(model.getDate());
                if (model.getVisible()){
                    details.setVisibility(View.VISIBLE);
                    accept.setVisibility(View.GONE);
                    deny.setVisibility(View.GONE);
                }
                else{
                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           approveComplaint(model.getId(), model.getMunicity());
                        }
                    });
                    deny.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteComplaint(model.getId(), model.getMunicity());
                        }
                    });
                }

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

    private void approveComplaint(String id, String municity) {
        DatabaseReference dbn = FirebaseDatabase.getInstance().getReference();
        dbn.child("Complaints").child(id).child("visible").setValue(true);
        FirebaseFirestore db = getInstance();
        Date date =  Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
        String formattedDate = formatter.format(date);
        if (municity == "Seleccionar municipio"){
            Map<String, Object> approveObject = new HashMap<>();
            approveObject.put("municity", "General");
            approveObject.put("date", formattedDate);
            approveObject.put("type", "complaint/approve");
            db.collection(getString(R.string.counters)).document().set(approveObject);
        } else{
            Map<String, Object> approveObject = new HashMap<>();
            approveObject.put("municity", municity);
            approveObject.put("date", formattedDate);
            approveObject.put("type", "complaint/approve");
            db.collection(getString(R.string.counters)).document().set(approveObject);
        }
    }

    private void deleteComplaint(String id, String municity) {
        DatabaseReference dbn = FirebaseDatabase.getInstance().getReference();
        dbn.child("Complaints").child(id).removeValue();
        FirebaseFirestore db = getInstance();
        Date date =  Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
        String formattedDate = formatter.format(date);
        if (municity == "Seleccionar municipio"){
            Map<String, Object> rejectObject = new HashMap<>();
            rejectObject.put("municity", "General");
            rejectObject.put("date", formattedDate);
            rejectObject.put("type", "complaint/reject");
            db.collection(getString(R.string.counters)).document().set(rejectObject);
        } else{
            Map<String, Object> rejectObject = new HashMap<>();
            rejectObject.put("municity", municity);
            rejectObject.put("date", formattedDate);
            rejectObject.put("type", "complaint/reject");
            db.collection(getString(R.string.counters)).document().set(rejectObject);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadComplaint() {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://petrescue-app.appspot.com");
        StorageReference reference = storage.getReference();
        final StorageReference refName = reference.child(Instant.now().toString());
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baas = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baas);
        byte[] data = baas.toByteArray();
        UploadTask uploadTask = refName.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                refName.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUri = task.getResult();
                        sendComplaint(downloadUri.toString());
                    }
                });
            }
        });


    }

    private void sendComplaint(String url) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
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
            ComplaintModel complaint = new ComplaintModel(name, ph1, ph2, address, stateSelectedBySpinner, Comments, formattedDate, key, url, false);
            db.child(getString(R.string.complaints_db)).child(key).setValue(complaint);
        }
        else{
            Toast.makeText(getContext(), "Revisa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        intentRes = data.getData();
        Bitmap img = null;
        try {
            img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), intentRes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(img);
        imageView.setVisibility(View.VISIBLE);
        send.setVisibility(View.VISIBLE);

    }

    private void getDetails(String id) {
        Intent nextActivity = new Intent(getActivity(), ComplaintActivity.class);
        nextActivity.putExtra("Id", id);
        startActivity(nextActivity);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        stateSelectedBySpinner = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}

