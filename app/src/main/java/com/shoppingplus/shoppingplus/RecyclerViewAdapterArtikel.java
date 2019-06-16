package com.shoppingplus.shoppingplus;
import android.app.DownloadManager;
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
import android.widget.Toast;

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

    @NonNull
    @Override
    public RecyclerViewAdapterArtikel.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewAdapterArtikel.MyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.cardview_artikel, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Artikel artikel = arrayArtikel.get(position);

        holder.nazivArtikla.setText(arrayArtikel.get(position).getNaziv_artikla());
        holder.kolicinaArtikla.setText(arrayArtikel.get(position).getKolicina_artikla());
        holder.opisArtikla.setText(arrayArtikel.get(position).getOpis_artikla());

        holder.cardView_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(context, PodrobnostiArtiklaActivity.class);
                /*intent.putExtra("Naziv_artikla", artikel.getNaziv_artikla());
                intent.putExtra("Kolicina_artikla", artikel.getKolicina_artikla());
                intent.putExtra("Opis_artikla", artikel.getOpis_artikla());
                //intent.putExtra("Status_artikla", artikel.getStatus_artikla());
                //intent.putExtra("id_artikla", artikel.getId_artikla());*/
                //intent.putExtra("id_kartice", artikel.getId_kartice());
                //context.startActivity(intent);

                //Toast.makeText(context, "onClick + pozicija " + position, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Za izbris je potrebno klikniti in držati artikel! ", Toast.LENGTH_SHORT).show();
            }
        });

        /* @Override
        public void onLongClick(View v, int i) {
            mquery.orderByChild("text")
                    .equalTo((String) notes.get(i).getName())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                firstChild.getRef().removeValue();
                            }
                        }

                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });*/
        holder.cardView_artikel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                //Toast.makeText(context, "onClick + pozicija " + position, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Artikel je bil odstranjen", Toast.LENGTH_SHORT).show();
                odstraniArtikel(arrayArtikel.get(position));

                return true;
            }
        });
    }

    private void odstraniArtikel(Artikel artikel){

        int position = arrayArtikel.indexOf(artikel);
        arrayArtikel.remove(position);
        notifyItemRemoved(position);

    }


    @Override
    public int getItemCount() {
        return arrayArtikel.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nazivArtikla;
        private TextView kolicinaArtikla;
        private TextView opisArtikla;
        CardView cardView_artikel ;
        //private LinearLayout artikel_item_id ;

        public MyViewHolder(View itemView) {
            super(itemView);
            nazivArtikla = (TextView) itemView.findViewById(R.id.rvNazivArtikla);
            kolicinaArtikla = (TextView) itemView.findViewById(R.id.rvKolicinaArtikla);
            opisArtikla = (TextView) itemView.findViewById(R.id.rvOpisArtikla);

            cardView_artikel = (CardView) itemView.findViewById(R.id.cardview_artikel_id);
            //artikel_item_id = (LinearLayout) itemView.findViewById(R.id.artikel_item_id);
        }
    }




}