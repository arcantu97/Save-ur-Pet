package arcan.apps.petrescue.holders;

import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import arcan.apps.petrescue.R;

public class CardViewHolder extends RecyclerView.ViewHolder{

    public TextView petName;
    public ImageView petImage;
    public Button adoptPet;
    public Button rescuePet;
    public TextView timer;


    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
        petName = itemView.findViewById(R.id.petName);
        petImage = itemView.findViewById(R.id.petImage);
        adoptPet = itemView.findViewById(R.id.Adoptar);
        rescuePet = itemView.findViewById(R.id.Rescatar);
        timer = itemView.findViewById(R.id.timer);
    }


}
