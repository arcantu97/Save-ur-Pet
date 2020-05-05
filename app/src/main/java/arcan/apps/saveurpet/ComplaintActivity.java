package arcan.apps.saveurpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import arcan.apps.saveurpet.fragments.DenunciasFragment;
import arcan.apps.saveurpet.models.ComplaintModel;

public class ComplaintActivity extends AppCompatActivity{

    MaterialTextView name, c1, c2, comments, address;
    ImageView photo;
    MaterialButton close;
    String id;
    TextView queja;
    String stateSelectedBySpinner;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        Bundle extras = getIntent().getExtras();
        id = extras.getString("Id");
        photo = findViewById(R.id.imageComplaint);
        name = findViewById(R.id.personNameInputLayout);
        c1 = findViewById(R.id.contactPhone1InputLayout);
        c2 = findViewById(R.id.contactPhone2InputLayout);
        address = findViewById(R.id.addressInputLayout);
        comments = findViewById(R.id.comentsInput);
        close = findViewById(R.id.close);
        queja = findViewById(R.id.Title);
        queja.setText(R.string.complaint_title_header);



        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.complaints_db)).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ComplaintModel model = dataSnapshot.getValue(ComplaintModel.class);
                Picasso.get().load(model.getUrlImage()).fit().into(photo);
                name.setText(String.format("Usuario: %s", model.getUser()));
                c1.setText(String.format("Teléfono 1: %s", model.getPhone1()));
                c2.setText(String.format("Teléfono 2: %s", model.getPhone2()));
                address.setText(String.format("Direccion: %s", model.getAddress()));
                comments.setText(String.format("Descripción: %s", model.getDescription()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(nextActivity);
            }
        });

    }
}
