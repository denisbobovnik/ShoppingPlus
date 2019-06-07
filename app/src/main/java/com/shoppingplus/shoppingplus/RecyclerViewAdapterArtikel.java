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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterArtikel extends RecyclerView.Adapter<RecyclerViewAdapterArtikel.MyViewHolder> {

    private ArrayList<Artikel> arrayArtikel;
    private Context mContext;

    public RecyclerViewAdapterArtikel(Context context, ArrayList<Artikel> artikel) {
        this.arrayArtikel = artikel;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_linearlayout_artikel, parent, false);
        final RecyclerViewAdapterArtikel.MyViewHolder vHolder = new RecyclerViewAdapterArtikel.MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.rvNazivArtikla.setText(arrayArtikel.get(position).getNaziv_artikla());

 /*       holder.artikel_item_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, null);
                intent.putExtra("kolicina", artikel.getKolicina_artikla());
                intent.putExtra("opis", artikel.getOpis_artikla());
                intent.putExtra("status", artikel.getStatus_artikla());
                intent.putExtra("naziv", artikel.getNaziv_artikla());
                mContext.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return arrayArtikel.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        //TextView textViewName;
        private TextView rvNazivArtikla;
        private LinearLayout artikel_item_id ;

        public MyViewHolder(View itemView) {
            super(itemView);
            //textViewName = itemView.findViewById(R.id.kartica_naslov_id);
            rvNazivArtikla = (TextView) itemView.findViewById(R.id.rvNazivArtikla);
            artikel_item_id = (LinearLayout) itemView.findViewById(R.id.artikel_item_id);
        }
    }
}