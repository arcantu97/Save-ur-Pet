package arcan.apps.petrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import arcan.apps.petrescue.models.AdoptModel;
import arcan.apps.petrescue.models.RescueModel;

public class RescatarActivity extends AppCompatActivity {

    TextView adoptTitle;
    ImageView adoptImage;
    MaterialButton rescue;
    String petName, petImageURL;
    FirebaseAuth firebaseAuth;
    String uid;
    TextInputLayout namePerson, personAddress, phone1, phone2;
    String name, address, ph1, ph2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescatar);
        adoptTitle = findViewById(R.id.adoptTitle);
        getExtras();
        adoptImage = findViewById(R.id.petImageAdopt);
        adoptTitle.setText(getString(R.string.rescue_pet_title_header).concat(" " + petName));
        Picasso.get().load(petImageURL).into(adoptImage);
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        loadInstances(uid);
    }

    private void loadInstances(final String uid) {
        rescue = findViewById(R.id.RescuePet);
        namePerson = findViewById(R.id.personNameInputLayout);
        personAddress = findViewById(R.id.addressInputLayout);
        phone1 = findViewById(R.id.contactPhone1InputLayout);
        phone2 = findViewById(R.id.contactPhone2InputLayout);
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        rescue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = namePerson.getEditText().getText().toString();
                address = personAddress.getEditText().getText().toString();
                ph1 = phone1.getEditText().getText().toString();
                ph2 = phone2.getEditText().getText().toString();
                RescueModel RescueForm = new RescueModel(name, address, RescatarActivity.this.uid, ph1, ph2, true, false);
                db.child(getString(R.string.petRescued_db)).child(uid).child(petName).setValue(RescueForm).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successfulRequest();
                    }
                });
            }
        });
    }

    private void successfulRequest() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.Successful_register_pet_title))
                .setMessage(getString(R.string.Successful_register_message))
                .setPositiveButton(getString(R.string.Ok_value), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent nextActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(nextActivity);
                    }
                }).show();
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        petName = bundle.getString("petName");
        petImageURL = bundle.getString("petImageURL");
    }
}
