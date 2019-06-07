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

public class RecyclerViewAdapterArtikel extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Artikel> arrayArtikel = new ArrayList<Artikel>() ;
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<Artikel> artikel) {
        this.arrayArtikel = artikel;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.listview_linearlayout_artikel, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final Artikel artikel = arrayArtikel.get(position);

        holder.artikel_item_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, null);
                intent.putExtra("kolicina", artikel.getKolicina_artikla());
                intent.putExtra("opis", artikel.getOpis_artikla());
                intent.putExtra("status", artikel.getStatus_artikla());

  //              Intent intent = new Intent(mContext,PodrobnostiKarticeActivity.class);
                // passing data to the PodrobnostiKartice activity
  //              intent.putExtra("Slika",artikel.getUrl_slike());
                //tip sifre za prikaz kode
   //             intent.putExtra("Tip_sifre",artikel.getTip_sifre());
   //             intent.putExtra("Sifra",artikel.getSifra_kartice());
                // intent.putExtra("Thumbnail",mData.get(position).getThumbnail());
                // start the activity
   //             mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayArtikel.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        //TextView textViewName;
        TextView rvNazivArtikla;
        LinearLayout artikel_item_id ;

        public MyViewHolder(View itemView) {
            super(itemView);
            //textViewName = itemView.findViewById(R.id.kartica_naslov_id);
            rvNazivArtikla = (TextView) itemView.findViewById(R.id.rvNazivArtikla);
            artikel_item_id = (LinearLayout) itemView.findViewById(R.id.artikel_item_id);
        }
    }
}