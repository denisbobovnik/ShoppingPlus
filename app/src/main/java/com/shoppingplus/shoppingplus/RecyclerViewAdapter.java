package com.shoppingplus.shoppingplus;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Kartica> arrayKartica;
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<Kartica> kartica) {
        this.arrayKartica = kartica;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.cardview_slika_kartice, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final Kartica kartica = arrayKartica.get(position);

        //holder.textViewName.setText(kartica.getNaziv_trgovine());
       Picasso.get()
                .load(kartica.getUrl_slike())
                .into(holder.slika_kartice);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PodrobnostiKarticeActivity.class);
                // passing data to the PodrobnostiKartice activity
                intent.putExtra("Slika",kartica.getUrl_slike());
                //tip sifre za prikaz kode
                intent.putExtra("Tip_sifre",kartica.getTip_sifre());
                intent.putExtra("Sifra",kartica.getSifra_kartice());
                intent.putExtra("id_kartice", kartica.getId_kartice());
                intent.putExtra("naziv_trgovine", kartica.getNaziv_trgovine());
               // intent.putExtra("Thumbnail",mData.get(position).getThumbnail());
                // start the activity
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayKartica.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        //TextView textViewName;
        ImageView slika_kartice;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);
            //textViewName = itemView.findViewById(R.id.kartica_naslov_id);
            slika_kartice = itemView.findViewById(R.id.kartica_slika_id);
            cardView = itemView.findViewById(R.id.cardview_id);
        }
    }
}