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

    private ArrayList<Artikel> arrayArtikel = new ArrayList<Artikel>();
    private Context context;

    public RecyclerViewAdapterArtikel(Context context, ArrayList<Artikel> artikel) {
        this.arrayArtikel = artikel;
        this.context = context;
    }

    /*@NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_linearlayout_artikel, parent, false);
        final RecyclerViewAdapterArtikel.MyViewHolder vHolder = new RecyclerViewAdapterArtikel.MyViewHolder(v);
        return vHolder;
    }*/

    @NonNull
    @Override
    public RecyclerViewAdapterArtikel.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewAdapterArtikel.MyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.cardview_artikel, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Artikel artikel = arrayArtikel.get(position);

        holder.nazivArtikla.setText(arrayArtikel.get(position).getNaziv_artikla());

        holder.cardView_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PodrobnostiArtiklaActivity.class);
                intent.putExtra("Naziv_artikla", artikel.getNaziv_artikla());
                intent.putExtra("Kolicina_artikla", artikel.getKolicina_artikla());
                intent.putExtra("Opis_artikla", artikel.getOpis_artikla());
                intent.putExtra("Status_artikla", artikel.getStatus_artikla());
                //intent.putExtra("id_artikla", artikel.getId_artikla());
                intent.putExtra("id_kartice", artikel.getId_kartice());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayArtikel.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nazivArtikla;
        CardView cardView_artikel ;
        //private LinearLayout artikel_item_id ;

        public MyViewHolder(View itemView) {
            super(itemView);
            nazivArtikla = (TextView) itemView.findViewById(R.id.rvNazivArtikla);
            cardView_artikel = (CardView) itemView.findViewById(R.id.cardview_artikel_id);
            //artikel_item_id = (LinearLayout) itemView.findViewById(R.id.artikel_item_id);
        }
    }
}