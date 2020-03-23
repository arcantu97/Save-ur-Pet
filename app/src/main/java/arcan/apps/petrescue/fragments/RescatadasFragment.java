package arcan.apps.petrescue.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import arcan.apps.petrescue.R;

public class RescatadasFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String uid;
    Long adminPermission;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrievePermission();
    }

    private void retrievePermission() {
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.userscollection_db))
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            adminPermission = (Long) task.getResult().get("adminPermission");
                            if (adminPermission == 1) {

                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rescatadas, container, false);
    }
}
