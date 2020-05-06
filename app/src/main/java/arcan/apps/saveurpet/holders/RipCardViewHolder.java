package arcan.apps.saveurpet.holders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import arcan.apps.saveurpet.R;

public class RipCardViewHolder extends RecyclerView.ViewHolder {

    public TextView petName;
    public ImageView petImage;
    public TextView requestDate;
    public TextView visitDate;
    public TextView requestState;
    public Button approve, reject;


    public RipCardViewHolder(@NonNull View itemView) {
        super(itemView);
        petName = itemView.findViewById(R.id.petNameAdopt);
        petImage = itemView.findViewById(R.id.petImageAdopt);
        requestDate = itemView.findViewById(R.id.requestDate);
        requestState = itemView.findViewById(R.id.requestState);
        visitDate = itemView.findViewById(R.id.visitDate);
        approve = itemView.findViewById(R.id.approve);
        reject = itemView.findViewById(R.id.reject);
    }
}
