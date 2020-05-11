package arcan.apps.saveurpet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StatisticsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spinner;
    ArrayAdapter<CharSequence> adapterCustom;
    Button entryDate, endDate, download, results;
    TextView entryDateText, endDateText;
    String municity;
    int counterAdoptedApprove = 0;
    int counterAdoptedReject = 0;
    int counterRescuedApprove = 0;
    int counterRescuedReject = 0;
    int counterComplaintApprove = 0;
    int counterComplaintReject = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        spinner = findViewById(R.id.SpinnerMun);
        entryDate = findViewById(R.id.entryDate);
        endDate = findViewById(R.id.endDate);
        entryDateText = findViewById(R.id.entryDateText);
        endDateText = findViewById(R.id.endDateText);
        download = findViewById(R.id.downloadBtn);
        adapterCustom = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapterCustom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCustom);
        spinner.setOnItemSelectedListener(this);
        final Calendar calendar =  Calendar.getInstance();
        final int day, month, year;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        entryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String date = formatter.format(calendar.getTime());
                        entryDateText.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_date));
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String date = formatter.format(calendar.getTime());
                        endDateText.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadContents();
                downloadGeneralPdf();
            }
        });

    }

    private void downloadGeneralPdf() {
        if(municity.equals("Seleccionar municipio")){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Descarga lista!")
                    .setMessage("Deseas descargar el pdf con el contenido de todos los municipios?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(StatisticsActivity.this, pdfActivity.class);
                            if (municity.equals("Seleccionar municipio")){
                                intent.putExtra("municity", "General");
                            }
                            else{
                                intent.putExtra("municity", municity);
                            }
                            intent.putExtra("startDate", entryDateText.getText().toString());
                            intent.putExtra("endDate", endDateText.getText().toString());
                            intent.putExtra("adoptedApproved", counterAdoptedApprove);
                            intent.putExtra("adoptedRejected", counterAdoptedReject);
                            intent.putExtra("rescuedApproved", counterRescuedApprove);
                            intent.putExtra("rescuedRejected", counterRescuedReject);
                            intent.putExtra("complaintsApproved", counterComplaintApprove);
                            intent.putExtra("complaintsRejected", counterComplaintReject);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    counterAdoptedApprove = 0;
                    counterAdoptedReject = 0;
                    counterRescuedApprove = 0;
                    counterRescuedReject = 0;
                    counterComplaintApprove = 0;
                    counterComplaintReject = 0;
                    entryDateText.setText("");
                    endDateText.setText("");
                    spinner.setSelection(0);
                    dialog.dismiss();
                }
            }).show();
        }
        else{
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Descarga lista!")
                    .setMessage("Deseas descargar el pdf con el contenido de la sucursal " + municity + "?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(StatisticsActivity.this, pdfActivity.class);
                            if (municity.equals("Seleccionar municipio")){
                                intent.putExtra("municity", "General");
                            }
                            else{
                                intent.putExtra("municity", municity);
                            }
                            intent.putExtra("startDate", entryDateText.getText().toString());
                            intent.putExtra("endDate", endDateText.getText().toString());
                            intent.putExtra("adoptedApproved", counterAdoptedApprove);
                            intent.putExtra("adoptedRejected", counterAdoptedReject);
                            intent.putExtra("rescuedApproved", counterRescuedApprove);
                            intent.putExtra("rescuedRejected", counterRescuedReject);
                            intent.putExtra("complaintsApproved", counterComplaintApprove);
                            intent.putExtra("complaintsRejected", counterComplaintReject);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    counterAdoptedApprove = 0;
                    counterAdoptedReject = 0;
                    counterRescuedApprove = 0;
                    counterRescuedReject = 0;
                    counterComplaintApprove = 0;
                    counterComplaintReject = 0;
                    entryDateText.setText("");
                    endDateText.setText("");
                    spinner.setSelection(0);
                    dialog.dismiss();
                }
            }).show();
        }



    }


    private void downloadContents() {
        if (municity.equals("Seleccionar municipio")){
            String entryDate, endDate;
            entryDate = entryDateText.getText().toString();
            endDate = endDateText.getText().toString();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionRef = db.collection("counters");
            Query queryAdoptionApprove = collectionRef.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("type", "adoption/approve").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryAdoptionApprove.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterAdoptedApprove++;
                        }
                    }
                }
            });
            Log.i("Counter", String.valueOf(counterAdoptedApprove));
            Query queryAdoptionReject = collectionRef.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("type", "adoption/reject").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryAdoptionReject.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterAdoptedReject++;
                        }
                    }
                }
            });
            Query queryRescueApprove = collectionRef.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("type", "rescue/approve").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryRescueApprove.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterRescuedApprove++;
                        }
                    }
                }
            });
            Query queryRescueReject = collectionRef.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("type", "rescue/reject").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryRescueReject.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterRescuedReject++;
                        }
                    }
                }
            });
            Query queryComplaintApprove = collectionRef.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("type", "complaint/approve").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryComplaintApprove.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterComplaintApprove++;
                        }
                    }
                }
            });
            Query queryComplaintReject = collectionRef.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("type", "complaint/reject").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryComplaintReject.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterComplaintReject++;
                        }
                    }
                }
            });
            downloadGeneralPdf();
        }
        else{
            String entryDate, endDate;
            entryDate = entryDateText.getText().toString();
            endDate = endDateText.getText().toString();
            Query queryaAM = FirebaseFirestore.getInstance().collection("counters").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("municity", municity).whereEqualTo("type", "adoption/approve").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryaAM.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterAdoptedApprove++;
                        }
                    }
                    Log.i("Counter", String.valueOf(counterAdoptedApprove));
                }
            });

            Query queryaRM = FirebaseFirestore.getInstance().collection("counters").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("municity", municity).whereEqualTo("type", "adoption/reject").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryaRM.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterAdoptedReject++;
                        }
                    }
                }
            });

            Query queryrAM = FirebaseFirestore.getInstance().collection("counters").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("municity", municity).whereEqualTo("type", "rescue/approve").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryrAM.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterRescuedApprove++;
                        }
                    }
                }
            });

            Query queryrRM = FirebaseFirestore.getInstance().collection("counters").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("municity", municity).whereEqualTo("type", "rescue/reject").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            queryrRM.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterRescuedReject++;
                        }
                    }
                }
            });

            Query querycAM = FirebaseFirestore.getInstance().collection("counters").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("municity", municity).whereEqualTo("type", "complaint/approve").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            querycAM.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterComplaintApprove++;
                        }
                    }
                }
            });

            Query querycRM = FirebaseFirestore.getInstance().collection("counters").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("municity", municity).whereEqualTo("type", "complaint/reject").whereGreaterThanOrEqualTo("date", entryDate).whereLessThanOrEqualTo("date", endDate);
            querycRM.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if (doc.exists()){
                            counterComplaintReject++;
                        }
                    }
                }
            });

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        municity = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
