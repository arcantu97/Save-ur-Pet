package arcan.apps.saveurpet;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import arcan.apps.saveurpet.models.AdoptModel;

public class AdoptarActivity extends AppCompatActivity {

    TextView adoptTitle;
    ImageView adoptImage;
    MaterialButton adopt, adoptDateButton;
    String petName, petImageURL, uN, uA, uP, municity;
    FirebaseAuth firebaseAuth;
    String uid;
    TextInputLayout namePerson, personAddress, phone1, phone2, adoptDate;
    TextInputEditText dateFinal;
    String name, address, ph1, ph2, Date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoptar);
        adoptTitle = findViewById(R.id.adoptTitle);
        getExtras();
        adoptImage = findViewById(R.id.petImageAdopt);
        adoptTitle.setText(getString(R.string.adopt_pet_title_header).concat(" " + petName));
        Picasso.get().load(petImageURL).into(adoptImage);
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        loadInstances(uid);
    }

    private void loadInstances(final String uid) {
        adopt = findViewById(R.id.AdoptPet);
        namePerson = findViewById(R.id.personNameInputLayout);
        personAddress = findViewById(R.id.addressInputLayout);
        phone1 = findViewById(R.id.contactPhone1InputLayout);
        phone2 = findViewById(R.id.contactPhone2InputLayout);
        adoptDate = findViewById(R.id.adoptDate);
        dateFinal = findViewById(R.id.dateFinal);
        adoptDateButton = findViewById(R.id.adoptDateButton);
        adoptDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar =  Calendar.getInstance();
                int day, month, year;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(AdoptarActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            adoptDate.setVisibility(View.VISIBLE);
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String date = formatter.format(calendar.getTime());
                            dateFinal.setText(date);
                        }
                    }, year, month, day);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3));
                    datePickerDialog.show();

            }
        });

        namePerson.getEditText().setText(uN);
        personAddress.getEditText().setText(uA);
        phone1.getEditText().setText(uP);
        adopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = namePerson.getEditText().getText().toString();
                address = personAddress.getEditText().getText().toString();
                ph1 = phone1.getEditText().getText().toString();
                ph2 = phone2.getEditText().getText().toString();
                Date = adoptDate.getEditText().getText().toString();
                AdoptModel AdoptForm = new AdoptModel(name, address, AdoptarActivity.this.uid, ph1, ph2, Date, Date, petName, petImageURL, municity, true, false);

                if (name.isEmpty() && address.isEmpty() && ph1.isEmpty() && ph2.isEmpty() && Date.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Dejaste algún campo vacío", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(getString(R.string.petAdopted_db)).document(petName).set(AdoptForm);
                    db.collection(getString(R.string.petcollection_db)).document(petName).update("nonRequested", true);
                    db.collection(getString(R.string.petcollection_db)).document(petName).update("requestAdoption", true);
                    successfulRequest();
                }
            }
        });
    }

    private void successfulRequest() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Gracias!")
                .setMessage(getString(R.string.Successful_message))
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
        uA = bundle.getString("userAddress");
        uN = bundle.getString("userName");
        uP = bundle.getString("userPhone");
        municity = bundle.getString("municity");
    }
}
