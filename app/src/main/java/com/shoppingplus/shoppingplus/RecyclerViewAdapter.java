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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Kartica kartica = arrayKartica.get(position);

       Picasso.get()
                .load(kartica.getUrl_slike())
                .into(holder.slika_kartice);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PodrobnostiKarticeActivity.class);
                intent.putExtra("Slika",kartica.getUrl_slike());
                intent.putExtra("Tip_sifre",kartica.getTip_sifre());
                intent.putExtra("Sifra",kartica.getSifra_kartice());
                intent.putExtra("id_kartice", kartica.getId_kartice());
                intent.putExtra("naziv_trgovine", kartica.getNaziv_trgovine());
                mContext.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                CollectionReference notesCollectionRef = db.collection("kartice");
                notesCollectionRef.document(kartica.getId_kartice())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Kartica uspešno izbrisana! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Kartice ni bilo mogoče izbrisati! ", Toast.LENGTH_SHORT).show();
                            }
                        });

                odstraniKartico(arrayKartica.get(holder.getAdapterPosition()));
                return true;
            }
        });
    }


    private void odstraniKartico(Kartica kartica){
        int position = arrayKartica.indexOf(kartica);
        arrayKartica.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return arrayKartica.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView slika_kartice;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);
            slika_kartice = itemView.findViewById(R.id.kartica_slika_id);
            cardView = itemView.findViewById(R.id.cardview_id);
        }
    }
}