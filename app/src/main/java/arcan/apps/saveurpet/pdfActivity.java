package arcan.apps.saveurpet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;

public class pdfActivity extends AppCompatActivity {
    String municity, entry, end;
    int counterAdoptedApprove;
    int counterAdoptedReject;
    int counterAdoptedTotal;
    int counterRescuedApprove;
    int counterRescuedReject;
    int counterRescuedTotal;
    int counterComplaintApprove;
    int counterComplaintReject;
    int counterComplaintTotal;
    TextView Municity, periodEntry, AdoptedA, AdoptedR, AdoptedT, RescuedA, RescuedR, RescuedT, ComplaintsA, ComplaintsR, ComplaintsT, adminSign;
    Button downloadPdf;
    private LinearLayout llPdf;
    private Bitmap bitmap;
    String adminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        Municity = findViewById(R.id.municityTitle);
        periodEntry = findViewById(R.id.periodTitle);
        AdoptedA = findViewById(R.id.aa);
        AdoptedR = findViewById(R.id.ar);
        AdoptedT = findViewById(R.id.at);
        RescuedA = findViewById(R.id.ra);
        RescuedR = findViewById(R.id.rr);
        RescuedT = findViewById(R.id.rt);
        ComplaintsA = findViewById(R.id.ca);
        ComplaintsR = findViewById(R.id.cr);
        ComplaintsT = findViewById(R.id.ct);
        adminSign = findViewById(R.id.adminSign);
        downloadPdf = findViewById(R.id.downloadBtn);
        llPdf = findViewById(R.id.layoutPdf);
        getExtras();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        adminName = sharedPref.getString("username", "Admin");

        if (municity.equals("General")){
            Municity.setVisibility(View.GONE);
        }
        else{
            Municity.setText(String.format("Sucursusal %s", municity));
        }

        periodEntry.setText(String.format("Del %s Al %s", entry, end));
        AdoptedA.setText(String.format("Aprobadas: %s", String.valueOf(counterAdoptedApprove)));
        AdoptedR.setText(String.format("Rechazadas: %s", String.valueOf(counterAdoptedReject)));
        counterAdoptedTotal = counterAdoptedApprove + counterAdoptedReject;
        AdoptedT.setText(String.format("Totales: %s", String.valueOf(counterAdoptedTotal)));

       RescuedA.setText(String.format("Aprobadas: %s", String.valueOf(counterRescuedApprove)));
        RescuedR.setText(String.format("Rechazadas: %s", String.valueOf(counterRescuedReject)));
        counterRescuedTotal = counterRescuedApprove + counterRescuedReject;
        RescuedT.setText(String.format("Totales: %s", String.valueOf(counterRescuedTotal)));


        ComplaintsA.setText(String.format("Aprobadas: %s", String.valueOf(counterComplaintApprove)));
        ComplaintsR.setText(String.format("Rechazadas: %s", String.valueOf(counterComplaintReject)));
        counterComplaintTotal = counterComplaintApprove + counterComplaintReject;
        ComplaintsT.setText(String.format("Totales: %s", String.valueOf(counterComplaintTotal)));

        adminSign.setText(String.format("Generado por %s", adminName));

        downloadPdf.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                    downloadPdf.setVisibility(View.GONE);
                    bitmap = loadBitmapFromView(llPdf, llPdf.getWidth(), llPdf.getHeight());
                    createPdf();
            }
        });


    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        municity = bundle.getString("municity");
        entry = bundle.getString("startDate");
        end = bundle.getString("endDate");
        counterAdoptedApprove = bundle.getInt("adoptedApproved");
        counterAdoptedReject = bundle.getInt("adoptedRejected");
        counterRescuedApprove = bundle.getInt("rescuedApproved");
        counterRescuedReject = bundle.getInt("rescuedRejected");
        counterComplaintApprove = bundle.getInt("complaintsApproved");
        counterComplaintReject = bundle.getInt("complaintsRejected");

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);
        @SuppressLint({"NewApi", "LocalSuppress"}) String dateToday = Instant.now().toString();

        // write the document content
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Reportes");
        if (!folder.exists()){
            folder.mkdirs();
        }
        @SuppressLint("SdCardPath") String targetPdf = Environment.getExternalStorageDirectory() + "/Reportes/" + dateToday + ".pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Algo ocurrió mal" + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "Se creó el pdf", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
