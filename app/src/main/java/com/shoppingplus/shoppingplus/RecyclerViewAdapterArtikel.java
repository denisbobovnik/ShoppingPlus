package com.shoppingplus.shoppingplus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class RecyclerViewAdapterArtikel extends RecyclerView.Adapter<RecyclerViewAdapterArtikel.MyViewHolder> {

    private ArrayList<Artikel> arrayArtikel;
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
                Toast.makeText(context, "Za izbris je potrebno klikniti in držati artikel! ", Toast.LENGTH_SHORT).show();
            }
        });

        holder.cardView_artikel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                CollectionReference notesCollectionRef = db.collection("artikli");
                notesCollectionRef.document(artikel.getId_artikla())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Artikel uspešno izbrisan! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Artikla ni bilo mogoče izbrisati! ", Toast.LENGTH_SHORT).show();
                            }
                        });

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

        public MyViewHolder(View itemView) {
            super(itemView);
            nazivArtikla = itemView.findViewById(R.id.rvNazivArtikla);
            kolicinaArtikla = itemView.findViewById(R.id.rvKolicinaArtikla);
            opisArtikla = itemView.findViewById(R.id.rvOpisArtikla);
            cardView_artikel = itemView.findViewById(R.id.cardview_artikel_id);
        }
    }
}