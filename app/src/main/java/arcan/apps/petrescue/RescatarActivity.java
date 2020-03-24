package arcan.apps.petrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import arcan.apps.petrescue.models.AdoptModel;
import arcan.apps.petrescue.models.RescueModel;

public class RescatarActivity extends AppCompatActivity {

    TextView rescueTitle;
    ImageView rescueImage;
    MaterialButton rescue, rescueDateButton;
    String petName, petImageURL;
    FirebaseAuth firebaseAuth;
    String uid;
    TextInputLayout namePerson, personAddress, phone1, phone2, rescueDate;
    TextInputEditText dateFinal;
    String name, address, ph1, ph2, Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescatar);
        rescueTitle = findViewById(R.id.rescueTitle);
        getExtras();
        rescueImage = findViewById(R.id.petImageRescue);
        rescueTitle.setText(getString(R.string.rescue_pet_title_header).concat(" " + petName));
        Picasso.get().load(petImageURL).into(rescueImage);
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
        rescueDate = findViewById(R.id.rescueDate);
        dateFinal = findViewById(R.id.dateFinal);
        rescueDateButton = findViewById(R.id.rescueDateButton);
        rescueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar =  Calendar.getInstance();
                int day, month, year;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(RescatarActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        rescueDate.setVisibility(View.VISIBLE);
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String date = formatter.format(calendar.getTime());
                        dateFinal.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        rescue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = namePerson.getEditText().getText().toString();
                address = personAddress.getEditText().getText().toString();
                ph1 = phone1.getEditText().getText().toString();
                ph2 = phone2.getEditText().getText().toString();
                Date = rescueDate.getEditText().getText().toString();
                RescueModel RescueForm = new RescueModel(name, address, RescatarActivity.this.uid, ph1, ph2, Date, Date, petName, petImageURL, true, false);

                if (name.isEmpty() && address.isEmpty() && ph1.isEmpty() && ph2.isEmpty() && Date.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Dejaste algún campo vacío", Toast.LENGTH_SHORT).show();
                }
                else{
                    db.child(getString(R.string.petRescued_db)).child(petName).setValue(RescueForm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.child(getString(R.string.petcollection_db)).child(petName).child("nonRequested").setValue(true);
                            db.child(getString(R.string.petcollection_db)).child(petName).child("requestRescue").setValue(true);
                            successfulRequest();
                        }
                    });
                }
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
